/**
 * Today's date as yyyy-MM-dd in the user's local time zone.
 * (new Date().toISOString() would give the UTC date, which is "yesterday" just after midnight in Tunisia.)
 */
export function todayIso(): string {
  const now = new Date();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  return `${now.getFullYear()}-${month}-${day}`;
}
