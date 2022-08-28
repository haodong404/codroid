import { Component, ComponentChild, h } from "preact";
import { TailwindColor } from "../../pkg/tailwindColor";
import SvgIcon from "../svgIcon";
import styles from "./index.module.css";

export interface NavigatorItemProps {
  class?: string;
  icon?: string;
  title?: string;
  selected?: boolean;
  color?: TailwindColor;
  onclick?: (e: Event) => void;
}

export class NavigatorItem extends Component<NavigatorItemProps> {
  render(): ComponentChild {
    let selected = "border-transparent";
    if (this.props.selected) {
      selected = "border-primary-900";
    }
    return (
      <div
        class={`${selected} border-2 m-1 p-0.5 rounded-3xl transition-all`}
        onClick={this.props.onclick}
      >
        <div
          class={`${styles.item_root} ${this.props.class}`}
          style={`background-color: ${this.props.color?.[100]}`}
        >
          <SvgIcon
            class={`h-8 w-8`}
            style={`fill: ${this.props.color?.[900]}`}
            name={this.props.icon}
          ></SvgIcon>
          <span
            class="-mt-1 text-base font-sans font-medium"
            style={`color:${this.props.color?.[900]}`}
          >
            {this.props.title}
          </span>
        </div>
      </div>
    );
  }
}
