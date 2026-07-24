/**
 * Status sesji GPW liczony po stronie klienta (bez backendu).
 * Notowania ciągłe: 09:00–17:00, poniedziałek–piątek, strefa Europe/Warsaw.
 * Uwzględnia dni wolne od handlu (święta stałe + ruchome liczone z Wielkanocy).
 */
export interface MarketStatus {
  open: boolean;
  label: string;      // "Otwarta" / "Zamknięta"
  warsawTime: string; // "HH:mm" czasu warszawskiego
  detail: string;     // tekst pomocniczy, np. "Zamknięcie o 17:00 (za 2h 14min)"
}

const OPEN_MINUTES = 9 * 60;   // 09:00
const CLOSE_MINUTES = 17 * 60; // 17:00

function formatLeft(minutes: number): string {
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return h > 0 ? `${h}h ${m}min` : `${m}min`;
}

/** Niedziela wielkanocna (kalendarz gregoriański) — algorytm Meeusa/Butchera. */
function easterSunday(year: number): Date {
  const a = year % 19;
  const b = Math.floor(year / 100);
  const c = year % 100;
  const d = Math.floor(b / 4);
  const e = b % 4;
  const f = Math.floor((b + 8) / 25);
  const g = Math.floor((b - f + 1) / 3);
  const h = (19 * a + b - d - g + 15) % 30;
  const i = Math.floor(c / 4);
  const k = c % 4;
  const l = (32 + 2 * e + 2 * i - h - k) % 7;
  const m = Math.floor((a + 11 * h + 22 * l) / 451);
  const month = Math.floor((h + l - 7 * m + 114) / 31); // 3 = marzec, 4 = kwiecień
  const day = ((h + l - 7 * m + 114) % 31) + 1;
  return new Date(year, month - 1, day);
}

function addDays(date: Date, days: number): Date {
  const d = new Date(date);
  d.setDate(d.getDate() + days);
  return d;
}

/** Klucz "MM-DD" z daty. */
function monthDay(date: Date): string {
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  return `${mm}-${dd}`;
}

/** Dni bez sesji na GPW w danym roku (zbiór kluczy "MM-DD"). */
function gpwHolidays(year: number): Set<string> {
  const easter = easterSunday(year);
  const movable = [
    addDays(easter, 1),  // Poniedziałek Wielkanocny
    addDays(easter, 60), // Boże Ciało
  ];
  const fixed = [
    '01-01', // Nowy Rok
    '01-06', // Trzech Króli
    '05-01', // Święto Pracy
    '05-03', // Święto Konstytucji 3 Maja
    '08-15', // Wniebowzięcie NMP
    '11-01', // Wszystkich Świętych
    '11-11', // Święto Niepodległości
    '12-24', // Wigilia (GPW nie handluje)
    '12-25', // Boże Narodzenie
    '12-26', // drugi dzień świąt
    '12-31', // Sylwester (GPW nie handluje)
  ];
  return new Set([...fixed, ...movable.map(monthDay)]);
}

export function getMarketStatus(now: Date = new Date()): MarketStatus {
  // Odczyt zegara ściennego w Warszawie niezależnie od strefy użytkownika.
  const warsaw = new Date(now.toLocaleString('en-US', { timeZone: 'Europe/Warsaw' }));
  const weekday = warsaw.getDay(); // 0 = niedziela ... 6 = sobota
  const minutes = warsaw.getHours() * 60 + warsaw.getMinutes();

  const hh = String(warsaw.getHours()).padStart(2, '0');
  const mm = String(warsaw.getMinutes()).padStart(2, '0');
  const warsawTime = `${hh}:${mm}`;

  const isWeekend = weekday === 0 || weekday === 6;
  const isHoliday = gpwHolidays(warsaw.getFullYear()).has(monthDay(warsaw));

  if (isWeekend || isHoliday) {
    return {
      open: false,
      label: 'Zamknięta',
      warsawTime,
      detail: isHoliday ? 'Dzień wolny od handlu' : 'Weekend — brak sesji',
    };
  }

  const open = minutes >= OPEN_MINUTES && minutes < CLOSE_MINUTES;

  let detail: string;
  if (open) {
    detail = `Zamknięcie o 17:00 (za ${formatLeft(CLOSE_MINUTES - minutes)})`;
  } else if (minutes < OPEN_MINUTES) {
    detail = `Otwarcie o 9:00 (za ${formatLeft(OPEN_MINUTES - minutes)})`;
  } else {
    detail = 'Po zamknięciu sesji';
  }

  return { open, label: open ? 'Otwarta' : 'Zamknięta', warsawTime, detail };
}
