import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IBlogPost } from 'app/shared/model/blog-post.model';

@Component({
    selector: 'jhi-blog-post-detail',
    templateUrl: './blog-post-detail.component.html'
})
export class BlogPostDetailComponent implements OnInit {
    blogPost: IBlogPost;

    constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ blogPost }) => {
            this.blogPost = blogPost;
        });
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }
    previousState() {
        window.history.back();
    }
}
