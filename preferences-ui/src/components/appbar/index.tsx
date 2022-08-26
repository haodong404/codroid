import { Component, ComponentChild, h } from "preact";
import styles from "./index.module.css";
import backIcon from "@assets/arrow_back.svg";
import { Button } from "../button";

export class Appbar extends Component {
  render(): ComponentChild {
    return (
      <div class="flex items-center pt-2 pb-2 w-full select-none">
        <img class="h-7 inline-block" src={backIcon} alt="back" />
        <h1 class="inline-block font-sans text-2xl ml-3 flex-1">Preferences</h1>
        <Button></Button>
      </div>
    );
  }
}
