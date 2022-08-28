import { Component, ComponentChild, h } from "preact";
import { TailwindColor } from "../../pkg/tailwindColor";
import SvgIcon from "../svgIcon";
import styles from "./index.module.css";

export interface ItemProps {
  class?: string;
  icon?: string;
  title?: string;
  color?: TailwindColor;
}

export class NavigatorItem extends Component<ItemProps> {
  render(): ComponentChild {
    return (
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
    );
  }
}
