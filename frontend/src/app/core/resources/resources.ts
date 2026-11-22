import { inject, Service, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IArrivalMode, IPathology, ITriageColor } from './resources.model';
import { ApiResponse, Page } from '../generics/ApiResponse.model';

const API_BASE_URL = 'http://localhost:3000';

@Service()
export class Resources {
  readonly #http = inject(HttpClient);
  readonly #triageColors = signal<ITriageColor[]>([]);
  readonly #pathologies = signal<IPathology[]>([]);
  readonly #arrivalModes = signal<IArrivalMode[]>([]);

  triageColors = this.#triageColors.asReadonly();
  pathologies = this.#pathologies.asReadonly();
  arrivalModes = this.#arrivalModes.asReadonly();

  public fetch() {
    this.fetchPathologies();
    this.fetchTriageColors();
    this.fetchArrivalModes();
  }

  private fetchPathologies() {
    this.#http
      .get<ApiResponse<Page<IPathology>>>(`${API_BASE_URL}/resources/pathologies`)
      .subscribe({
        next: (res) => {
          this.#pathologies.set(res.data?.content ?? []);
        },
        error: (err) => {
          console.error('Failed to fetch pathologies:', err);
        },
      });
  }

  private fetchTriageColors() {
    this.#http
      .get<ApiResponse<ITriageColor[]>>(`${API_BASE_URL}/resources/triage-colors`)
      .subscribe({
        next: (res) => {
          this.#triageColors.set(res.data ?? []);
        },
        error: (err) => {
          console.error('Failed to fetch triage colors:', err);
        },
      });
  }

  private fetchArrivalModes() {
    this.#http
      .get<ApiResponse<IArrivalMode[]>>(`${API_BASE_URL}/resources/arrival-modes`)
      .subscribe({
        next: (res) => {
          this.#arrivalModes.set(res.data ?? []);
        },
        error: (err) => {
          console.error('Failed to fetch arrival modes:', err);
        },
      });
  }
}
