export const mapName = (worldName) => {
    if (worldName === "world") return "Overworld";
    if (worldName === "world_nether") return "Nether";
    if (worldName === "world_the_end") return "The End";
    return worldName;
}

export const mapTime = (minecraftTime) => {
    const time = minecraftTime % 24000;
    if (time < 0) return "Error";

    if (time < 1000) return "Sunrise";
    if (time < 12000) return "Day";
    if (time < 13000) return "Sunset";
    if (time < 23000) return "Night";
    return "Sunrise";
}

export const timeMarks = [
    { value: 1000, label: "Day" },
    { value: 13000, label: "Night" },
    { value: 23000, label: "Sunrise" }
]