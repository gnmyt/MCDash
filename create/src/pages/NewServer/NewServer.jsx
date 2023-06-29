import {useState} from "react";
import {Box, Button, Step, StepLabel, Stepper} from "@mui/material";
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
    const [instanceId, setInstanceId] = useState(Math.random().toString(36).substring(2, 7));

    const handleNext = () => {
        if (currentStep === steps.length) return;
        setCurrentStep((prevActiveStep) => prevActiveStep + 1);
    }

    const handleBack = () => {
        setCurrentStep((prevActiveStep) => prevActiveStep - 1);
    }

    return (
        <>
            <Stepper activeStep={currentStep}>
                {steps.map((label, index) => <Step key={index} completed={index < currentStep} color={"secondary"}>
                        <StepLabel>{label}</StepLabel>
                    </Step>
                )}
            </Stepper>

            <Box sx={{mb: 2, mt: 2}}>
                {currentStep === 0 && <Server software={software} setSoftware={setSoftware} serverName={serverName}
                                              version={version} setVersion={setVersion} setServerName={setServerName}
                                              instanceId={instanceId} setInstanceId={setInstanceId}/>}
                {currentStep === 1 && <Game />}
                {currentStep === 2 && <Account />}

                {currentStep === steps.length && <Finished />}
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