interface AnsiSegment {
    text: string;
    style: React.CSSProperties;
}

const ANSI_COLORS: { [key: number]: string } = {
    30: '#000000',
    31: '#ff5555',
    32: '#55ff55',
    33: '#ffff55',
    34: '#5555ff',
    35: '#ff55ff',
    36: '#55ffff',
    37: '#ffffff',
    90: '#555555',
    91: '#ff5555',
    92: '#55ff55',
    93: '#ffff55',
    94: '#5555ff',
    95: '#ff55ff',
    96: '#55ffff',
    97: '#ffffff',
};

const ANSI_BG_COLORS: { [key: number]: string } = {
    40: '#000000',
    41: '#ff5555',
    42: '#55ff55',
    43: '#ffff55',
    44: '#5555ff',
    45: '#ff55ff',
    46: '#55ffff',
    47: '#ffffff',
    100: '#555555',
    101: '#ff5555',
    102: '#55ff55',
    103: '#ffff55',
    104: '#5555ff',
    105: '#ff55ff',
    106: '#55ffff',
    107: '#ffffff',
};

export function parseAnsi(text: string): AnsiSegment[] {
    const segments: AnsiSegment[] = [];
    const ansiRegex = /\x1b\[([0-9;]*)m/g;
    
    let lastIndex = 0;
    let currentStyle: React.CSSProperties = {};
    let match;

    while ((match = ansiRegex.exec(text)) !== null) {
        if (match.index > lastIndex) {
            const textBefore = text.slice(lastIndex, match.index);
            if (textBefore) {
                segments.push({ text: textBefore, style: { ...currentStyle } });
            }
        }

        const codes = match[1].split(';').map(Number);
        currentStyle = applyAnsiCodes(codes, currentStyle);

        lastIndex = ansiRegex.lastIndex;
    }

    if (lastIndex < text.length) {
        segments.push({ text: text.slice(lastIndex), style: { ...currentStyle } });
    }

    if (segments.length === 0) {
        segments.push({ text, style: {} });
    }

    return segments;
}

function applyAnsiCodes(codes: number[], currentStyle: React.CSSProperties): React.CSSProperties {
    const style = { ...currentStyle };

    for (let i = 0; i < codes.length; i++) {
        const code = codes[i];

        if (code === 0) {
            return {};
        } else if (code === 1) {
            style.fontWeight = 'bold';
        } else if (code === 2) {
            style.opacity = 0.5;
        } else if (code === 3) {
            style.fontStyle = 'italic';
        } else if (code === 4) {
            style.textDecoration = 'underline';
        } else if (code === 9) {
            style.textDecoration = 'line-through';
        } else if (code === 22) {
            delete style.fontWeight;
            delete style.opacity;
        } else if (code === 23) {
            delete style.fontStyle;
        } else if (code === 24 || code === 29) {
            delete style.textDecoration;
        } else if (code >= 30 && code <= 37) {
            style.color = ANSI_COLORS[code];
        } else if (code === 38) {
            if (codes[i + 1] === 5) {
                const colorIndex = codes[i + 2];
                style.color = get256Color(colorIndex);
                i += 2;
            } else if (codes[i + 1] === 2) {
                const r = codes[i + 2];
                const g = codes[i + 3];
                const b = codes[i + 4];
                style.color = `rgb(${r}, ${g}, ${b})`;
                i += 4;
            }
        } else if (code === 39) {
            delete style.color;
        } else if (code >= 40 && code <= 47) {
            style.backgroundColor = ANSI_BG_COLORS[code];
        } else if (code === 48) {
            if (codes[i + 1] === 5) {
                const colorIndex = codes[i + 2];
                style.backgroundColor = get256Color(colorIndex);
                i += 2;
            } else if (codes[i + 1] === 2) {
                const r = codes[i + 2];
                const g = codes[i + 3];
                const b = codes[i + 4];
                style.backgroundColor = `rgb(${r}, ${g}, ${b})`;
                i += 4;
            }
        } else if (code === 49) {
            delete style.backgroundColor;
        } else if (code >= 90 && code <= 97) {
            style.color = ANSI_COLORS[code];
        } else if (code >= 100 && code <= 107) {
            style.backgroundColor = ANSI_BG_COLORS[code];
        }
    }

    return style;
}

function get256Color(index: number): string {
    if (index < 16) {
        const colors = [
            '#000000', '#800000', '#008000', '#808000', '#000080', '#800080', '#008080', '#c0c0c0',
            '#808080', '#ff0000', '#00ff00', '#ffff00', '#0000ff', '#ff00ff', '#00ffff', '#ffffff'
        ];
        return colors[index];
    } else if (index < 232) {
        const i = index - 16;
        const r = Math.floor(i / 36);
        const g = Math.floor((i % 36) / 6);
        const b = i % 6;
        const toHex = (v: number) => (v === 0 ? 0 : 55 + v * 40);
        return `rgb(${toHex(r)}, ${toHex(g)}, ${toHex(b)})`;
    } else {
        const gray = (index - 232) * 10 + 8;
        return `rgb(${gray}, ${gray}, ${gray})`;
    }
}

export function stripAnsi(text: string): string {
    return text.replace(/\x1b\[[0-9;]*m/g, '');
}
