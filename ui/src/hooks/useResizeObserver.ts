import { useCallback, useRef, useState, useEffect } from "react";

interface Size {
    width: number;
    height: number;
}

export function useResizeObserver<T extends HTMLElement>(): [
    React.RefCallback<T>,
    Size
] {
    const [size, setSize] = useState<Size>({ width: 0, height: 0 });
    const resizeObserverRef = useRef<ResizeObserver | null>(null);
    const elementRef = useRef<T | null>(null);

    const ref = useCallback((node: T | null) => {
        if (resizeObserverRef.current) {
            resizeObserverRef.current.disconnect();
        }

        if (node) {
            elementRef.current = node;
            resizeObserverRef.current = new ResizeObserver((entries) => {
                for (const entry of entries) {
                    const { width, height } = entry.contentRect;
                    setSize({ width, height });
                }
            });
            resizeObserverRef.current.observe(node);

            const rect = node.getBoundingClientRect();
            setSize({ width: rect.width, height: rect.height });
        }
    }, []);

    useEffect(() => {
        return () => {
            if (resizeObserverRef.current) {
                resizeObserverRef.current.disconnect();
            }
        };
    }, []);

    return [ref, size];
}
