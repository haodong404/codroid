import { Component, ComponentChild, h } from "preact";
import SvgIcon from "../svgIcon";
import styles from "./index.module.css";

export class SearchBar extends Component<{ class?: string }> {
  render(): ComponentChild {
    return (
      <div class={`${styles.searchbar_root} ${this.props.class}`}>
        <input
          class="focus:outline-0 bg-transparent flex-auto"
          placeholder="Searching for settings..."
        ></input>
        <SvgIcon name="search" class="h-7 w-7 fill-primary-800" />
      </div>
    );
  }
}
