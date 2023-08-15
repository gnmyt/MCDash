import {t} from "i18next";

export const mapName = (worldName) => {
    if (worldName === "world") return "Overworld";
    if (worldName === "world_nether") return "Nether";
    if (worldName === "world_the_end") return "The End";
    return worldName;
}

export const mapTime = (minecraftTime) => {
    const time = minecraftTime % 24000;

    if (time < 1000) return t("time.sunrise");
    if (time < 12000) return t("time.day");
    if (time < 13000) return t("time.sunset");
    if (time < 23000) return t("time.night");
    return "Sunrise";
}

export const timeMarks = () => [
    { value: 1000, label: t("time.day") },
    { value: 13000, label: t("time.night") },
    { value: 23000, label: t("time.sunrise") }
]