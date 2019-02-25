import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { PersonalOffCmsSharedModule } from 'app/shared';
import {
    CommentComponent,
    CommentDetailComponent,
    CommentUpdateComponent,
    CommentDeletePopupComponent,
    CommentDeleteDialogComponent,
    commentRoute,
    commentPopupRoute
} from './';

const ENTITY_STATES = [...commentRoute, ...commentPopupRoute];

@NgModule({
    imports: [PersonalOffCmsSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        CommentComponent,
        CommentDetailComponent,
        CommentUpdateComponent,
        CommentDeleteDialogComponent,
        CommentDeletePopupComponent
    ],
    entryComponents: [CommentComponent, CommentUpdateComponent, CommentDeleteDialogComponent, CommentDeletePopupComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PersonalOffCmsCommentModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
