import { Component, OnInit } from '@angular/core';
import { Beneficio, Transferencia } from '../../models/beneficio.model';
import { BeneficioService } from '../../services/beneficio.service';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-beneficio-list',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './beneficio-list.component.html',
  styleUrls: ['./beneficio-list.component.css'],
})
export class BeneficioListComponent implements OnInit {
  beneficios: Beneficio[] = [];
  beneficioEditando: Beneficio | null = null;
  novoBeneficio: Beneficio = this.criarBeneficioVazio();
  transferencia: Transferencia = { fromId: 0, toId: 0, amount: 0 };
  mensagem: string = '';
  erro: string = '';
  carregando: boolean = false;
  valorTotal: number = 0;

  constructor(private beneficioService: BeneficioService) {}

  ngOnInit(): void {
    this.carregarBeneficios();
    this.carregarValorTotal();
  }

  carregarBeneficios(): void {
    this.carregando = true;
    this.beneficioService.listar().subscribe({
      next: (data) => {
        this.beneficios = data;
        this.carregando = false;
      },
      error: (err) => {
        this.erro = 'Erro ao carregar benefícios: ' + err;
        this.carregando = false;
      },
    });
  }

  carregarValorTotal(): void {
    this.beneficioService.getValorTotalAtivos().subscribe({
      next: (total) => (this.valorTotal = total),
      error: (err) => console.error('Erro ao carregar valor total:', err),
    });
  }

  criarBeneficio(): void {
    this.mensagem = '';
    this.erro = '';

    this.beneficioService.criar(this.novoBeneficio).subscribe({
      next: (beneficio) => {
        this.beneficios.push(beneficio);
        this.novoBeneficio = this.criarBeneficioVazio();
        this.mensagem = 'Benefício criado com sucesso!';
        this.carregarValorTotal();
      },
      error: (err) => {
        this.erro = 'Erro ao criar benefício: ' + err;
      },
    });
  }

  editarBeneficio(beneficio: Beneficio): void {
    this.beneficioEditando = { ...beneficio };
  }

  salvarEdicao(): void {
    if (this.beneficioEditando && this.beneficioEditando.id) {
      this.beneficioService.atualizar(this.beneficioEditando.id, this.beneficioEditando).subscribe({
        next: (beneficioAtualizado) => {
          const index = this.beneficios.findIndex((b) => b.id === beneficioAtualizado.id);
          if (index !== -1) {
            this.beneficios[index] = beneficioAtualizado;
          }
          this.beneficioEditando = null;
          this.mensagem = 'Benefício atualizado com sucesso!';
          this.carregarValorTotal();
        },
        error: (err) => {
          this.erro = 'Erro ao atualizar benefício: ' + err;
        },
      });
    }
  }

  deletarBeneficio(id: number): void {
    if (confirm('Tem certeza que deseja deletar este benefício?')) {
      this.beneficioService.deletar(id).subscribe({
        next: () => {
          this.beneficios = this.beneficios.filter((b) => b.id !== id);
          this.mensagem = 'Benefício deletado com sucesso!';
          this.carregarValorTotal();
        },
        error: (err) => {
          this.erro = 'Erro ao deletar benefício: ' + err;
        },
      });
    }
  }

  realizarTransferencia(): void {
    this.mensagem = '';
    this.erro = '';

    this.beneficioService.transferir(this.transferencia).subscribe({
      next: () => {
        this.mensagem = 'Transferência realizada com sucesso!';
        this.transferencia = { fromId: 0, toId: 0, amount: 0 };
        this.carregarBeneficios();
        this.carregarValorTotal();
      },
      error: (err) => {
        this.erro = err;
      },
    });
  }

  cancelarEdicao(): void {
    this.beneficioEditando = null;
  }

  private criarBeneficioVazio(): Beneficio {
    return {
      nome: '',
      descricao: '',
      valor: 0,
      ativo: true,
    };
  }

  getBeneficiosAtivos(): Beneficio[] {
    return this.beneficios.filter((b) => b.ativo);
  }
}
