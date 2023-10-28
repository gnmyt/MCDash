import {useState} from "react";
import {Alert, Box, Button, Step, StepLabel, Stepper} from "@mui/material";
import Server from "./components/Server";
import Game from "./components/Game";
import Account from "./components/Account";
import Finished from "./components/Finished";

const steps = ["Server", "Game", "Account"];

export const NewServer = () => {

    const [currentStep, setCurrentStep] = useState(0);

    const [software, setSoftware] = useState("spigot");
    const [version, setVersion] = useState("1.20.1");
    const [serverName, setServerName] = useState("My server");
    const [memory, setMemory] = useState(4);

    const [eula, setEula] = useState(false);
    const [mcPort, setMcPort] = useState(25565);
    const [panelPort, setPanelPort] = useState(7867);

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState(Math.random().toString(36).substring(2, 8));

    const [error, setError] = useState(null);

    const handleNext = () => {
        if (currentStep === steps.length) return;

        if (currentStep === 0 && (!software || !version || !serverName || !memory)) {
            setError("You must fill in all fields to continue");
            return;
        }

        if (currentStep === 1 && (!mcPort || !panelPort)) {
            setError("You must fill in all fields to continue");
            return;
        }

        if (currentStep === 1 && !eula) {
            setError("You must accept the EULA to continue");
            return;
        }

        if (currentStep === 2 && (!username || !password)) {
            setError("You must fill in all fields to continue");
            return;
        }

        setCurrentStep((prevActiveStep) => prevActiveStep + 1);
        setError(null);
    }

    const handleBack = () => {
        setCurrentStep((prevActiveStep) => prevActiveStep - 1);
        setError(null);
    }

    return (
        <>
            <Stepper activeStep={currentStep} sx={{mb: 2}}>
                {steps.map((label, index) => <Step key={index} completed={index < currentStep} color={"secondary"}>
                        <StepLabel>{label}</StepLabel>
                    </Step>
                )}
            </Stepper>

            {error && <Alert severity="error">{error}</Alert>}

            <Box sx={{mt: 2, mb: 2}}>
                {currentStep === 0 && <Server software={software} setSoftware={setSoftware} serverName={serverName}
                                              version={version} setVersion={setVersion} setServerName={setServerName}
                                              memory={memory} setMemory={setMemory}/>}
                {currentStep === 1 && <Game eula={eula} setEula={setEula} mcPort={mcPort} setMcPort={setMcPort}
                                            panelPort={panelPort} setPanelPort={setPanelPort}/>}
                {currentStep === 2 && <Account username={username} setUsername={setUsername} password={password}
                                               setPassword={setPassword}/>}

                {currentStep === steps.length && <Finished software={software} version={version} serverName={serverName}
                                                           memory={memory} mcPort={mcPort} panelPort={panelPort}
                                                           username={username} password={password}/>}
            </Box>

            <Box sx={{display: 'flex', flexDirection: 'row'}}>
                <Button color="inherit" disabled={currentStep === 0} onClick={handleBack} sx={{mr: 1}}>
                    Back
                </Button>

                <Box sx={{flex: '1 1 auto'}}/>

                {currentStep !== steps.length && <Button onClick={handleNext} variant="contained">
                    {currentStep === steps.length - 1 ? 'Finish' : 'Next'}
                </Button>}
            </Box>
        </>
    )
}