import { IBlogPost } from 'app/shared/model/blog-post.model';

export interface ITag {
    id?: number;
    name?: string;
    blogPosts?: IBlogPost[];
}

export class Tag implements ITag {
    constructor(public id?: number, public name?: string, public blogPosts?: IBlogPost[]) {}
}
