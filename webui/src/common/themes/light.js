import { createTheme } from '@mui/material/styles';

const theme = createTheme({
    palette: {
        mode: 'light',
        primary: {
            main: '#556cd6',
        },
        background: {
            card: '#f1f1f1',
            default: '#F4F6F8',
            darker: "#e5e5e5",
        }
    },
    components: {
        MuiAppBar: {
            styleOverrides: {
                root: {
                    backgroundColor: '#ffffff'
                }
            }
        },
        MuiTypography: {
            styleOverrides: {
                root: {
                    color: '#000000'
                }
            }
        },
        MuiListItemButton: {
            styleOverrides: {
                root: {
                    borderRadius: 15,
                    margin: 4,
                    marginLeft: 7,
                    marginRight: 7
                }
            }
        },
        MuiCssBaseline: {
            styleOverrides: {
                body: {
                    "&::-webkit-scrollbar, & *::-webkit-scrollbar": {
                        width: 8,
                    },
                    "&::-webkit-scrollbar-thumb, & *::-webkit-scrollbar-thumb": {
                        borderRadius: 8,
                        minHeight: 24,

                        backgroundColor: "#adadb0",
                    },
                    "&::-webkit-scrollbar-thumb:hover, & *::-webkit-scrollbar-thumb:hover": {
                        backgroundColor: "#3B3A4A"
                    }
                },
            },
        },
    },
    shape: {
        borderRadius: 8,
    }
});

export default theme;