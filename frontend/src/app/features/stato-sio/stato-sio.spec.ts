import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { StatoSio } from './stato-sio';
import { StatoServiziSio } from './stato-servizi-sio/stato-servizi-sio';

describe('StatoSio', () => {
  let component: StatoSio;
  let fixture: ComponentFixture<StatoSio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatoSio],
      providers: [provideHttpClient(), provideHttpClientTesting(), StatoServiziSio],
    }).compileComponents();

    fixture = TestBed.createComponent(StatoSio);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
