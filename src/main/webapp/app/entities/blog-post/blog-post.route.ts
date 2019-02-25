import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { BlogPost } from 'app/shared/model/blog-post.model';
import { BlogPostService } from './blog-post.service';
import { BlogPostComponent } from './blog-post.component';
import { BlogPostDetailComponent } from './blog-post-detail.component';
import { BlogPostUpdateComponent } from './blog-post-update.component';
import { BlogPostDeletePopupComponent } from './blog-post-delete-dialog.component';
import { IBlogPost } from 'app/shared/model/blog-post.model';

@Injectable({ providedIn: 'root' })
export class BlogPostResolve implements Resolve<IBlogPost> {
    constructor(private service: BlogPostService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IBlogPost> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<BlogPost>) => response.ok),
                map((blogPost: HttpResponse<BlogPost>) => blogPost.body)
            );
        }
        return of(new BlogPost());
    }
}

export const blogPostRoute: Routes = [
    {
        path: '',
        component: BlogPostComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'personalOffCmsApp.blogPost.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: BlogPostDetailComponent,
        resolve: {
            blogPost: BlogPostResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'personalOffCmsApp.blogPost.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: BlogPostUpdateComponent,
        resolve: {
            blogPost: BlogPostResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'personalOffCmsApp.blogPost.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: BlogPostUpdateComponent,
        resolve: {
            blogPost: BlogPostResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'personalOffCmsApp.blogPost.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const blogPostPopupRoute: Routes = [
    {
        path: ':id/delete',
        component: BlogPostDeletePopupComponent,
        resolve: {
            blogPost: BlogPostResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'personalOffCmsApp.blogPost.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
