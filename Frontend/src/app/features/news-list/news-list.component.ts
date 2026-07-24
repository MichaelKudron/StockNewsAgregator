import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ArticleService } from '../../core/services/article.service';
import { NewsItem } from '../../core/models/article.model';

@Component({
  selector: 'app-news-list',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterLink],
  templateUrl: './news-list.component.html',
  styleUrl: './news-list.component.scss',
})
export class NewsListComponent implements OnInit {
  private articleService = inject(ArticleService);

  news = signal<NewsItem[]>([]);
  loading = signal(true);
  error = signal(false);

  ngOnInit(): void {
    this.articleService.getLatestArticles().subscribe({
      next: list => {
        this.news.set(list);
        this.loading.set(false);
      },
      error: () => {
        this.error.set(true);
        this.loading.set(false);
      },
    });
  }
}
