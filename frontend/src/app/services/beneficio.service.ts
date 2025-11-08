import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Beneficio, Transferencia } from '../models/beneficio.model';

@Injectable({
  providedIn: 'root',
})
export class BeneficioService {
  private apiUrl = 'http://localhost:8000/api/beneficios';

  constructor(private http: HttpClient) {}

  listar(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(this.apiUrl).pipe(catchError(this.handleError));
  }

  buscarPorId(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.apiUrl}/${id}`).pipe(catchError(this.handleError));
  }

  criar(beneficio: Beneficio): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.apiUrl, beneficio).pipe(catchError(this.handleError));
  }

  atualizar(id: number, beneficio: Beneficio): Observable<Beneficio> {
    return this.http
      .put<Beneficio>(`${this.apiUrl}/${id}`, beneficio)
      .pipe(catchError(this.handleError));
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(catchError(this.handleError));
  }

  transferir(transferencia: Transferencia): Observable<any> {
    return this.http
      .post(`${this.apiUrl}/transferir`, transferencia)
      .pipe(catchError(this.handleError));
  }

  listarAtivos(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(`${this.apiUrl}/ativos`).pipe(catchError(this.handleError));
  }

  getValorTotalAtivos(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/total-ativos`).pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    console.error(error);
    let errorMessage = 'Erro desconhecido!';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Erro: ${error.error.message}`;
    } else {
      errorMessage = `Erro ${error.status}: ${error.error || error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => errorMessage);
  }
}
