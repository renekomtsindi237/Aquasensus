const CACHE = 'aquasensus-l4-v1';
const PRECACHE = [
  '/',
  '/index.html',
  '/manifest.webmanifest',
  '/assets/brand/aquasensus-logo.png',
  '/assets/brand/aquasensus-mark-192.png',
  '/assets/brand/aquasensus-mark-512.png'
];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE).then((cache) => cache.addAll(PRECACHE).catch(() => undefined))
  );
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((cles) =>
      Promise.all(cles.filter((c) => c !== CACHE).map((c) => caches.delete(c)))
    )
  );
});

self.addEventListener('fetch', (event) => {
  if (event.request.method !== 'GET') {
    return;
  }
  const url = new URL(event.request.url);
  if (url.pathname.startsWith('/api/')) {
    return;
  }
  event.respondWith(
    caches.match(event.request).then((cached) => {
      if (cached) {
        return cached;
      }
      return fetch(event.request)
        .then((reponse) => {
          const copie = reponse.clone();
          if (reponse.ok && url.origin === self.location.origin) {
            caches.open(CACHE).then((cache) => cache.put(event.request, copie));
          }
          return reponse;
        })
        .catch(() => caches.match('/index.html'));
    })
  );
});
