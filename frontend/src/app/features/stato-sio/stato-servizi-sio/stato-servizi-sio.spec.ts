import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { StatoServiziSio } from './stato-servizi-sio';

describe('StatoServiziSio', () => {
  let service: StatoServiziSio;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), StatoServiziSio],
    });
    service = TestBed.inject(StatoServiziSio);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
