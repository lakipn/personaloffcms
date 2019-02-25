export interface IComment {
    id?: number;
    name?: string;
    body?: any;
    email?: string;
    blogPostCaption?: string;
    blogPostId?: number;
}

export class Comment implements IComment {
    constructor(
        public id?: number,
        public name?: string,
        public body?: any,
        public email?: string,
        public blogPostCaption?: string,
        public blogPostId?: number
    ) {}
}
