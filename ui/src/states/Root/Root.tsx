import { TokenContext } from "@/contexts/TokenContext";
import {useContext} from "react";
import {Navigate} from "react-router-dom";

const Root = () => {

    const {tokenValid} = useContext(TokenContext)!;

    return (
        <>
            {tokenValid === false && <Navigate to="/login" />}

            {tokenValid === true && <div>Root</div>}
        </>
    )
}

export default Root;