import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import axe from 'axe-core';
import { AppComponent } from './app.component';
import { FileComponent } from './file/file.component';
import { PointsComponent } from './points/points.component';
import { SignalerComponent } from './signaler/signaler.component';

async function assertAxe(fixtureRoot: HTMLElement, nom: string): Promise<void> {
  const results = await axe.run(fixtureRoot, {
    rules: { 'color-contrast': { enabled: false } }
  });
  const graves = results.violations.filter((v) => v.impact === 'critical' || v.impact === 'serious');
  expect(graves.map((v) => v.id)).withContext(nom).toEqual([]);
}

describe('Accessibilité ISS-063', () => {
  it('coquille : lien d’évitement et navigation nommée', async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [provideRouter([]), provideHttpClient()]
    }).compileComponents();
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.querySelector('a.skip')?.getAttribute('href')).toBe('#contenu');
    expect(el.querySelector('nav')?.getAttribute('aria-label')).toBe('Navigation principale');
  });

  it('axe-core sur le signalement', async () => {
    await TestBed.configureTestingModule({
      imports: [SignalerComponent],
      providers: [provideRouter([]), provideHttpClient()]
    }).compileComponents();
    const fixture = TestBed.createComponent(SignalerComponent);
    fixture.detectChanges();
    await assertAxe(fixture.nativeElement, 'signaler');
  });

  it('axe-core sur la file délégué', async () => {
    await TestBed.configureTestingModule({
      imports: [FileComponent],
      providers: [provideRouter([]), provideHttpClient()]
    }).compileComponents();
    const fixture = TestBed.createComponent(FileComponent);
    fixture.detectChanges();
    await assertAxe(fixture.nativeElement, 'file');
  });

  it('axe-core sur la liste d’ouvrages', async () => {
    await TestBed.configureTestingModule({
      imports: [PointsComponent],
      providers: [provideRouter([]), provideHttpClient()]
    }).compileComponents();
    const fixture = TestBed.createComponent(PointsComponent);
    fixture.detectChanges();
    await assertAxe(fixture.nativeElement, 'points');
  });
});
