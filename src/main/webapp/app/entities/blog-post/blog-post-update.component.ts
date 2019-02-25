import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { IBlogPost } from 'app/shared/model/blog-post.model';
import { BlogPostService } from './blog-post.service';
import { ITag } from 'app/shared/model/tag.model';
import { TagService } from 'app/entities/tag';

@Component({
    selector: 'jhi-blog-post-update',
    templateUrl: './blog-post-update.component.html'
})
export class BlogPostUpdateComponent implements OnInit {
    blogPost: IBlogPost;
    isSaving: boolean;

    tags: ITag[];
    dateAndTime: string;

    constructor(
        protected dataUtils: JhiDataUtils,
        protected jhiAlertService: JhiAlertService,
        protected blogPostService: BlogPostService,
        protected tagService: TagService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ blogPost }) => {
            this.blogPost = blogPost;
            this.dateAndTime = this.blogPost.dateAndTime != null ? this.blogPost.dateAndTime.format(DATE_TIME_FORMAT) : null;
        });
        this.tagService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<ITag[]>) => mayBeOk.ok),
                map((response: HttpResponse<ITag[]>) => response.body)
            )
            .subscribe((res: ITag[]) => (this.tags = res), (res: HttpErrorResponse) => this.onError(res.message));
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }

    setFileData(event, entity, field, isImage) {
        this.dataUtils.setFileData(event, entity, field, isImage);
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.blogPost.dateAndTime = this.dateAndTime != null ? moment(this.dateAndTime, DATE_TIME_FORMAT) : null;
        if (this.blogPost.id !== undefined) {
            this.subscribeToSaveResponse(this.blogPostService.update(this.blogPost));
        } else {
            this.subscribeToSaveResponse(this.blogPostService.create(this.blogPost));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IBlogPost>>) {
        result.subscribe((res: HttpResponse<IBlogPost>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackTagById(index: number, item: ITag) {
        return item.id;
    }

    getSelected(selectedVals: Array<any>, option: any) {
        if (selectedVals) {
            for (let i = 0; i < selectedVals.length; i++) {
                if (option.id === selectedVals[i].id) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }
}
