import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Resources } from './resources';

describe('Resources', () => {
  let service: Resources;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(Resources);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
