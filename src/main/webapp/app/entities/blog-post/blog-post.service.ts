import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IBlogPost } from 'app/shared/model/blog-post.model';

type EntityResponseType = HttpResponse<IBlogPost>;
type EntityArrayResponseType = HttpResponse<IBlogPost[]>;

@Injectable({ providedIn: 'root' })
export class BlogPostService {
    public resourceUrl = SERVER_API_URL + 'api/blog-posts';

    constructor(protected http: HttpClient) {}

    create(blogPost: IBlogPost): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(blogPost);
        return this.http
            .post<IBlogPost>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(blogPost: IBlogPost): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(blogPost);
        return this.http
            .put<IBlogPost>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IBlogPost>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IBlogPost[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    protected convertDateFromClient(blogPost: IBlogPost): IBlogPost {
        const copy: IBlogPost = Object.assign({}, blogPost, {
            dateAndTime: blogPost.dateAndTime != null && blogPost.dateAndTime.isValid() ? blogPost.dateAndTime.toJSON() : null
        });
        return copy;
    }

    protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
        if (res.body) {
            res.body.dateAndTime = res.body.dateAndTime != null ? moment(res.body.dateAndTime) : null;
        }
        return res;
    }

    protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        if (res.body) {
            res.body.forEach((blogPost: IBlogPost) => {
                blogPost.dateAndTime = blogPost.dateAndTime != null ? moment(blogPost.dateAndTime) : null;
            });
        }
        return res;
    }
}
