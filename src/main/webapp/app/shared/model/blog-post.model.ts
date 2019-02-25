import { Moment } from 'moment';
import { IComment } from 'app/shared/model/comment.model';
import { ITag } from 'app/shared/model/tag.model';

export interface IBlogPost {
    id?: number;
    caption?: string;
    slug?: string;
    body?: any;
    dateAndTime?: Moment;
    comments?: IComment[];
    tags?: ITag[];
}

export class BlogPost implements IBlogPost {
    constructor(
        public id?: number,
        public caption?: string,
        public slug?: string,
        public body?: any,
        public dateAndTime?: Moment,
        public comments?: IComment[],
        public tags?: ITag[]
    ) {}
}
