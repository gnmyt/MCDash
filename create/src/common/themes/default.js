import { createTheme } from "@mui/material/styles";

const theme = createTheme({
    palette: {
        mode: "dark",
        background: {
            default: "#27232F",
            darker: "#1A1722",
        },
        primary: {
            main: "#ce93d8",
        }
    },
    shape: {
        borderRadius: 10,
    },
    typography: {
        fontFamily: ["Inter", "sans-serif",].join(",")
    }
});

export default theme;