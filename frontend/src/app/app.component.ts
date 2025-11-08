import { Component } from '@angular/core';
import { BeneficioListComponent } from './components/beneficio-list/beneficio-list.component';

@Component({
  selector: 'app-root',
  template: `
    <div class="min-vh-100 bg-light">
      <nav class="navbar navbar-dark bg-dark shadow-sm">
        <div class="container">
          <span class="navbar-brand mb-0 h1">
            <i class="fas fa-gift me-2"></i>Sistema de Benefícios
          </span>
          <span class="navbar-text"> <i class="fas fa-rocket me-1"></i>v1.0.0 </span>
        </div>
      </nav>

      <main class="container-fluid py-4">
        <app-beneficio-list></app-beneficio-list>
      </main>

      <footer class="bg-dark text-light py-3 mt-5">
        <div class="container text-center">
          <small>
            <i class="fas fa-code me-1"></i>
            Desenvolvido com Angular & Spring Boot
          </small>
        </div>
      </footer>
    </div>
  `,
  styles: [
    `
      .min-vh-100 {
        min-height: 100vh;
        display: flex;
        flex-direction: column;
      }

      main {
        flex: 1;
      }
    `,
  ],
  standalone: true, // ← ADICIONE
  imports: [BeneficioListComponent],
})
export class AppComponent {
  title = 'Sistema de Benefícios';
}
