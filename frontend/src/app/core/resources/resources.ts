import { inject, Service, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IPathology, TApiResponsePathology } from './resources.models';
import { SIOResourcesService } from '../api';

@Service()
export class Resources {
  readonly #http = inject(HttpClient);
  readonly #sioResources = inject(SIOResourcesService);

  readonly #listaPatologie = signal<IPathology[]>([]);
  readonly listaPatologie = this.#listaPatologie.asReadonly();

  public fetchPatologie(){
    this.#http.get<TApiResponsePathology>('http://localhost:3000/resources/pathologies')
      .subscribe({
        next: (res) => {
          this.#listaPatologie.set(res.data.content);
        },
        error: (error) => {
          console.error(error);
        }
      })
  }

  public fetchPathoFromApi(){
    this.#sioResources.getPathologies({}).subscribe({
      next: (res) => {
        this.#listaPatologie.set(res.data?.content ?? []);
      },
      error: (error) => {
        console.error(error);
      }
    });
  }
}
