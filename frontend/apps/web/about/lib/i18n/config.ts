import i18next from 'i18next'
import { initReactI18next } from 'react-i18next'
import HttpBackend from 'i18next-http-backend';

i18next
  .use(HttpBackend)            // load JSON từ /public/locales
  .use(initReactI18next)       // connect với react-i18next
  .init({
    lng: 'en',                 // ngôn ngữ mặc định
    fallbackLng: 'en',
    supportedLngs: ['en', 'vi','kr','jp'],
    ns: ['common'],
    defaultNS: 'common',
    interpolation: { escapeValue: false },
    backend: {
      loadPath: '/locales/{{lng}}/{{ns}}.json',
    },
    react: {
      useSuspense: false,      // tắt suspense nếu muốn
    },
  })

export default i18next
