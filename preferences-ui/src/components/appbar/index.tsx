import { Component, ComponentChild, h } from "preact";
import styles from "./index.module.css";
import { Button } from "../button";
import SvgIcon from "../SvgIcon";

export class Appbar extends Component {
  render(): ComponentChild {
    return (
      <div class="flex items-center pt-2 pb-2 w-full select-none">
        <SvgIcon class="h-6 w-6 inline-block" name="arrow_back" />
        <h1 class="inline-block font-sans text-2xl ml-3 flex-1">Preferences</h1>
        <Button></Button>
      </div>
    );
  }
}
