import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { PersonalOffCmsSharedModule } from 'app/shared';
import {
    BlogPostComponent,
    BlogPostDetailComponent,
    BlogPostUpdateComponent,
    BlogPostDeletePopupComponent,
    BlogPostDeleteDialogComponent,
    blogPostRoute,
    blogPostPopupRoute
} from './';

const ENTITY_STATES = [...blogPostRoute, ...blogPostPopupRoute];

@NgModule({
    imports: [PersonalOffCmsSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        BlogPostComponent,
        BlogPostDetailComponent,
        BlogPostUpdateComponent,
        BlogPostDeleteDialogComponent,
        BlogPostDeletePopupComponent
    ],
    entryComponents: [BlogPostComponent, BlogPostUpdateComponent, BlogPostDeleteDialogComponent, BlogPostDeletePopupComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PersonalOffCmsBlogPostModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
