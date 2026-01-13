/**
 * Convert the size of the file to a human-readable format
 * @param size size of the file in bytes
 */
export const convertSize = (size: number) => {
    if (size < 1024) return size + " B";
    else if (size < 1024 * 1024) return (size / 1024).toFixed(2) + " KB";
    else if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(2) + " MB";
    else return (size / (1024 * 1024 * 1024)).toFixed(2) + " GB";
}

/**
 * Get the Monaco editor language from a filename
 * @param filename the name of the file
 */
export const getLanguageFromFilename = (filename: string): string => {
    const ext = filename.split('.').pop()?.toLowerCase() || '';
    
    const languageMap: Record<string, string> = {
        'js': 'javascript',
        'jsx': 'javascript',
        'ts': 'typescript',
        'tsx': 'typescript',
        'html': 'html',
        'htm': 'html',
        'css': 'css',
        'scss': 'scss',
        'less': 'less',
        'vue': 'vue',
        'svelte': 'svelte',

        'json': 'json',
        'yaml': 'yaml',
        'yml': 'yaml',
        'xml': 'xml',
        'toml': 'toml',
        'ini': 'ini',
        'properties': 'ini',

        'java': 'java',
        'py': 'python',
        'rb': 'ruby',
        'php': 'php',
        'c': 'c',
        'cpp': 'cpp',
        'cc': 'cpp',
        'cxx': 'cpp',
        'h': 'c',
        'hpp': 'cpp',
        'cs': 'csharp',
        'go': 'go',
        'rs': 'rust',
        'swift': 'swift',
        'kt': 'kotlin',
        'kts': 'kotlin',
        'scala': 'scala',
        'lua': 'lua',
        'r': 'r',

        'sh': 'shell',
        'bash': 'shell',
        'zsh': 'shell',
        'bat': 'bat',
        'cmd': 'bat',
        'ps1': 'powershell',

        'md': 'markdown',
        'markdown': 'markdown',
        'tex': 'latex',
        
        'sql': 'sql',
        
        'dockerfile': 'dockerfile',
        'graphql': 'graphql',
        'gql': 'graphql',
    };

    if (filename.toLowerCase() === 'dockerfile') return 'dockerfile';
    
    return languageMap[ext] || 'plaintext';
}