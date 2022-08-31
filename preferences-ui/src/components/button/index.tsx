import { Component, ComponentChild, h } from "preact";
import { TailwindColor } from "../../pkg/tailwindColor";
import SvgIcon from "../svgIcon";
import styles from "./index.module.css";

export interface ButtonProps {
  class?: string;
  color: TailwindColor;
  icon?: string;
  text: string;
  onclick?: (e?: Event) => void;
}

function renderIcon(props: any): h.JSX.Element {
  if (props.icon === null || props.icon === undefined) {
    return <></>;
  } else {
    return (
      <SvgIcon
        name={props.icon}
        class={props.class}
        style={props.style}
      ></SvgIcon>
    );
  }
}

export class Button extends Component<ButtonProps> {
  render(): ComponentChild {
    return (
      <button
        onClick={this.props.onclick}
        class={`${styles.button_root} ${this.props.class}`}
        style={`border-color: ${this.props.color[900]}; background-color: ${this.props.color[50]};`}
      >
        {h(renderIcon, {
          icon: this.props.icon,
          class: "w-4 h-4 inline-block mr-1",
          style: `fill: ${this.props.color[900]}`,
        })}
        <span
          class="select-none text-xs font-sans font-semibold"
          style={`color: ${this.props.color[900]}`}
        >
          {this.props.text}
        </span>
      </button>
    );
  }
}
