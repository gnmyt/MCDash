import { useState, useCallback } from "react";

interface Player {
    name: string;
}

interface UsePlayerSelectionOptions<T extends Player> {
    players: T[];
}

export function usePlayerSelection<T extends Player>({ players }: UsePlayerSelectionOptions<T>) {
    const [selectedPlayers, setSelectedPlayers] = useState<string[]>([]);
    const [targetPlayer, setTargetPlayer] = useState<string | null>(null);

    const togglePlayer = useCallback((name: string) => {
        setSelectedPlayers(prev =>
            prev.includes(name) ? prev.filter(p => p !== name) : [...prev, name]
        );
    }, []);

    const toggleAll = useCallback(() => {
        if (selectedPlayers.length === players.length) {
            setSelectedPlayers([]);
        } else {
            setSelectedPlayers(players.map(p => p.name));
        }
    }, [selectedPlayers.length, players]);

    const clearSelection = useCallback(() => {
        setSelectedPlayers([]);
        setTargetPlayer(null);
    }, []);

    const getPlayersToAction = useCallback((): string[] => {
        return targetPlayer ? [targetPlayer] : selectedPlayers;
    }, [targetPlayer, selectedPlayers]);

    const isAllSelected = players.length > 0 && selectedPlayers.length === players.length;
    const hasSelection = selectedPlayers.length > 0;

    return {
        selectedPlayers,
        targetPlayer,
        setTargetPlayer,
        togglePlayer,
        toggleAll,
        clearSelection,
        getPlayersToAction,
        isAllSelected,
        hasSelection,
    };
}
