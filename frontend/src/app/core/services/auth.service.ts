import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest, RegisterRequest, LoginResponse, User } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = '/api/auth';
  private readonly TOKEN_KEY = 'taskmaster_token';
  private readonly USER_KEY = 'taskmaster_user';

  // Signals for state management
  private _currentUser = signal<User | null>(this.getUserFromStorage());
  private _isAuthenticated = computed(() => !!this._currentUser());

  // Public computed signals
  readonly currentUser = this._currentUser.asReadonly();
  readonly isAuthenticated = this._isAuthenticated;

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API_URL}/login`, credentials)
      .pipe(
        tap(response => this.setSession(response))
      );
  }

  register(userData: RegisterRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API_URL}/register`, userData)
      .pipe(
        tap(response => this.setSession(response))
      );
  }

  logout(): void {
    this.clearSession();
  }

  getToken(): string | null {
    if (typeof window !== 'undefined' && window.localStorage) {
      return localStorage.getItem(this.TOKEN_KEY);
    }
    return null;
  }

  private setSession(response: LoginResponse): void {
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.setItem(this.TOKEN_KEY, response.token);
      
      // Convert response to User object
      const user: User = {
        userId: response.userId,
        name: response.name,
        email: response.email,
        userCreatedAt: response.userCreatedAt
      };
      
      localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    }
    
    // Create user object for signal
    const currentUser: User = {
      userId: response.userId,
      name: response.name,
      email: response.email,
      userCreatedAt: response.userCreatedAt
    };
    
    this._currentUser.set(currentUser);
  }

  private clearSession(): void {
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.USER_KEY);
    }
    this._currentUser.set(null);
  }

  private getUserFromStorage(): User | null {
    if (typeof window !== 'undefined' && window.localStorage) {
      // Limpar dados antigos que podem estar causando conflito
      localStorage.removeItem('currentUser');
      
      const userStr = localStorage.getItem(this.USER_KEY);
      if (userStr) {
        try {
          return JSON.parse(userStr);
        } catch (error) {
          localStorage.removeItem(this.USER_KEY);
          return null;
        }
      }
    }
    return null;
  }
}
