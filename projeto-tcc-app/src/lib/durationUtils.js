export function formatDuration(durationInDays) {
    const ONE_DAY = 1;
    const ONE_HOUR = 1 / 24;
    const ONE_MINUTE = 1 / (24 * 60);

    if (durationInDays < ONE_HOUR) {
        // Menor que 1 hora, exibir em minutos
        const minutes = durationInDays / ONE_MINUTE;
        return `${minutes.toFixed(2).replace('.', ',')} minutos`;
    } else if (durationInDays < ONE_DAY) {
        // Menor que 1 dia, exibir em horas
        const hours = durationInDays / ONE_HOUR;
        return `${hours.toFixed(2).replace('.', ',')} horas`;
    } else {
        // Maior ou igual a 1 dia, exibir em dias
        return `${durationInDays.toFixed(2).replace('.', ',')} dias`;
    }
}
