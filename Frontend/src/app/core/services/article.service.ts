import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Article, CompanyArticle } from '../models/article.model';
import { environment } from '../../../environments/environment';

/** ArticleService zwraca JSON w snake_case — mapujemy na camelCase */
interface CompanyArticleWire {
  article_id: string;
  article_title: string;
  references: string;
  match_level: CompanyArticle['matchLevel'];
}

interface ArticleWire {
  id: string;
  source_code: string;
  source_type: string | null;
  url: string;
  title: string;
  summary: string | null;
  content: string | null;
  content_length: number;
  author: string | null;
  category: string | null;
  content_source: Article['contentSource'];
  published_at: string | null;
  fetched_at: string | null;
  processing_status: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class ArticleService {
  private http = inject(HttpClient);
  private base = `${environment.articleApiUrl}/api/v1/article`;

  getCompanyArticles(companyId: string): Observable<CompanyArticle[]> {
    return this.http.get<CompanyArticleWire[]>(`${this.base}/company/${companyId}`).pipe(
      map(list =>
        list.map(a => ({
          articleId: a.article_id,
          articleTitle: a.article_title,
          references: a.references,
          matchLevel: a.match_level,
        }))
      )
    );
  }

  getArticle(id: string): Observable<Article> {
    return this.http.get<ArticleWire>(`${this.base}/${id}`).pipe(
      map(a => ({
        id: a.id,
        sourceCode: a.source_code,
        sourceType: a.source_type,
        url: a.url,
        title: a.title,
        summary: a.summary,
        content: a.content,
        contentLength: a.content_length,
        author: a.author,
        category: a.category,
        contentSource: a.content_source,
        publishedAt: a.published_at,
        fetchedAt: a.fetched_at,
        processingStatus: a.processing_status,
      }))
    );
  }
}
