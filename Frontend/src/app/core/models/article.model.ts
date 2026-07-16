export type MatchLevel = 'NONE' | 'MENTION' | 'TOPIC';

/** Element listy artykułów powiązanych ze spółką (GET /article/company/{id}) */
export interface CompanyArticle {
  articleId: string;
  articleTitle: string;
  /** wynik analizy sentymentu, np. "neutral" */
  sentiment: string;
  matchLevel: MatchLevel;
}

/** Pełny artykuł (GET /article/{id}) */
export interface Article {
  id: string;
  sourceCode: string;
  sourceType: string | null;
  url: string;
  title: string;
  summary: string | null;
  content: string | null;
  contentLength: number;
  author: string | null;
  category: string | null;
  contentSource: 'FULL_SCRAPED' | 'TRUNCATED_PAYWALL' | 'RSS_ONLY' | null;
  publishedAt: string | null;
  fetchedAt: string | null;
  processingStatus: string | null;
}
