export function formatDuration(durationInDays) {
    const ONE_DAY = 1;
    const ONE_HOUR = 1 / 24;
    const ONE_MINUTE = 1 / (24 * 60);

    if (durationInDays < ONE_HOUR) {
        const minutes = durationInDays / ONE_MINUTE;
        return `${minutes.toFixed(2).replace('.', ',')} minutes`;
    } else if (durationInDays < ONE_DAY) {
        const hours = durationInDays / ONE_HOUR;
        return `${hours.toFixed(2).replace('.', ',')} hours`;
    } else {
        return `${durationInDays.toFixed(2).replace('.', ',')} days`;
    }
}
