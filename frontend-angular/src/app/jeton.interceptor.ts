import { HttpInterceptorFn } from '@angular/common/http';

export const jetonInterceptor: HttpInterceptorFn = (req, next) => {
  const jeton = sessionStorage.getItem('aqs.jeton');
  if (!jeton) {
    return next(req);
  }
  return next(req.clone({ setHeaders: { Authorization: `Bearer ${jeton}` } }));
};
