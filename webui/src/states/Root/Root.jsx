import {Navigate, Outlet} from "react-router-dom";
import {TokenContext} from "@contexts/Token/index.js";
import {useContext} from "react";
import {Box, Toolbar} from "@mui/material";
import Sidebar from "@/states/Root/components/Sidebar";
import Header from "@/states/Root/components/Header";

export const Root = () => {
    const {tokenValid} = useContext(TokenContext);

    return (
        <div>
            {tokenValid === false && <Navigate to="/login" />}

            <Box sx={{ display: 'flex' }}>
                <Header />
                <Sidebar />
                <Box component="main" sx={{ pt: 1, pl: 2, pr: 1, pb: 1 }}>
                    <Toolbar />
                    <Outlet />
                </Box>
            </Box>
        </div>
    )
}