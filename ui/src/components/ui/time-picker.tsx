import * as React from "react";
import { ClockIcon } from "@phosphor-icons/react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from "./popover";

interface TimePickerProps {
    value: { hour: number; minute: number };
    onChange: (value: { hour: number; minute: number }) => void;
    className?: string;
    disabled?: boolean;
}

const TimePicker = React.forwardRef<HTMLButtonElement, TimePickerProps>(
    ({ value, onChange, className, disabled }, ref) => {
        const [isOpen, setIsOpen] = React.useState(false);

        const formatTime = (hour: number, minute: number) => {
            return `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
        };

        const hours = Array.from({ length: 24 }, (_, i) => i);
        const minutes = Array.from({ length: 12 }, (_, i) => i * 5);

        const handleHourClick = (hour: number) => {
            onChange({ ...value, hour });
        };

        const handleMinuteClick = (minute: number) => {
            onChange({ ...value, minute });
            setIsOpen(false);
        };

        return (
            <Popover open={isOpen} onOpenChange={setIsOpen}>
                <PopoverTrigger asChild>
                    <Button
                        ref={ref}
                        variant="outline"
                        role="combobox"
                        disabled={disabled}
                        className={cn(
                            "w-full justify-start text-left font-normal rounded-xl h-10",
                            !value && "text-muted-foreground",
                            className
                        )}
                    >
                        <ClockIcon className="mr-2 h-4 w-4" />
                        <span className="font-mono text-base">
                            {formatTime(value.hour, value.minute)}
                        </span>
                    </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0 rounded-xl" align="start">
                    <div className="flex">
                        <div className="border-r">
                            <div className="px-3 py-2 text-xs font-medium text-muted-foreground text-center border-b">
                                Hour
                            </div>
                            <div className="h-48 overflow-y-auto p-1">
                                <div className="grid grid-cols-4 gap-1">
                                    {hours.map((hour) => (
                                        <Button
                                            key={hour}
                                            variant={value.hour === hour ? "default" : "ghost"}
                                            size="sm"
                                            className={cn(
                                                "h-8 w-8 p-0 font-mono",
                                                value.hour === hour && "font-semibold"
                                            )}
                                            onClick={() => handleHourClick(hour)}
                                        >
                                            {hour.toString().padStart(2, '0')}
                                        </Button>
                                    ))}
                                </div>
                            </div>
                        </div>
                        <div>
                            <div className="px-3 py-2 text-xs font-medium text-muted-foreground text-center border-b">
                                Minute
                            </div>
                            <div className="h-48 overflow-y-auto p-1">
                                <div className="grid grid-cols-3 gap-1">
                                    {minutes.map((minute) => (
                                        <Button
                                            key={minute}
                                            variant={value.minute === minute ? "default" : "ghost"}
                                            size="sm"
                                            className={cn(
                                                "h-8 w-10 p-0 font-mono",
                                                value.minute === minute && "font-semibold"
                                            )}
                                            onClick={() => handleMinuteClick(minute)}
                                        >
                                            :{minute.toString().padStart(2, '0')}
                                        </Button>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </div>
                </PopoverContent>
            </Popover>
        );
    }
);

TimePicker.displayName = "TimePicker";

interface MinutePickerProps {
    value: number;
    onChange: (value: number) => void;
    className?: string;
    disabled?: boolean;
}

const MinutePicker = React.forwardRef<HTMLButtonElement, MinutePickerProps>(
    ({ value, onChange, className, disabled }, ref) => {
        const [isOpen, setIsOpen] = React.useState(false);

        const minutes = Array.from({ length: 60 }, (_, i) => i);
        const quickMinutes = [0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55];

        const handleMinuteClick = (minute: number) => {
            onChange(minute);
            setIsOpen(false);
        };

        return (
            <Popover open={isOpen} onOpenChange={setIsOpen}>
                <PopoverTrigger asChild>
                    <Button
                        ref={ref}
                        variant="outline"
                        role="combobox"
                        disabled={disabled}
                        className={cn(
                            "w-full justify-start text-left font-normal rounded-xl h-10",
                            className
                        )}
                    >
                        <ClockIcon className="mr-2 h-4 w-4" />
                        <span className="font-mono text-base">
                            :{value.toString().padStart(2, '0')}
                        </span>
                    </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0 rounded-xl" align="start">
                    <div className="p-2">
                        <div className="text-xs font-medium text-muted-foreground mb-2 px-1">
                            Quick select
                        </div>
                        <div className="grid grid-cols-4 gap-1 mb-2">
                            {quickMinutes.map((minute) => (
                                <Button
                                    key={minute}
                                    variant={value === minute ? "default" : "ghost"}
                                    size="sm"
                                    className={cn(
                                        "h-8 w-11 p-0 font-mono",
                                        value === minute && "font-semibold"
                                    )}
                                    onClick={() => handleMinuteClick(minute)}
                                >
                                    :{minute.toString().padStart(2, '0')}
                                </Button>
                            ))}
                        </div>
                        <div className="border-t pt-2">
                            <div className="text-xs font-medium text-muted-foreground mb-2 px-1">
                                All minutes
                            </div>
                            <div className="h-32 overflow-y-auto">
                                <div className="grid grid-cols-6 gap-1">
                                    {minutes.map((minute) => (
                                        <Button
                                            key={minute}
                                            variant={value === minute ? "default" : "ghost"}
                                            size="sm"
                                            className={cn(
                                                "h-7 w-8 p-0 font-mono text-xs",
                                                value === minute && "font-semibold"
                                            )}
                                            onClick={() => handleMinuteClick(minute)}
                                        >
                                            {minute.toString().padStart(2, '0')}
                                        </Button>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </div>
                </PopoverContent>
            </Popover>
        );
    }
);

MinutePicker.displayName = "MinutePicker";

export { TimePicker, MinutePicker };
