import en from './en.json';
import rw from './rw.json';

export type LanguageCode = 'en' | 'rw';

const translations: Record<LanguageCode, any> = { en, rw };

export function getTranslation(lang: LanguageCode, keyPath: string, fallback?: string): string {
  const keys = keyPath.split('.');
  let current = translations[lang] || translations.en;
  for (const k of keys) {
    if (current && typeof current === 'object' && k in current) {
      current = current[k];
    } else {
      // fallback to English
      let fallbackCurrent = translations.en;
      for (const fbKey of keys) {
        if (fallbackCurrent && typeof fallbackCurrent === 'object' && fbKey in fallbackCurrent) {
          fallbackCurrent = fallbackCurrent[fbKey];
        } else {
          return fallback || keyPath;
        }
      }
      return typeof fallbackCurrent === 'string' ? fallbackCurrent : (fallback || keyPath);
    }
  }
  return typeof current === 'string' ? current : (fallback || keyPath);
}
