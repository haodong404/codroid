import { Component, ComponentChild, h } from "preact";
import styles from "./index.module.css";

interface TextfieldProps {
  class?: string;
  placeholder?: string;
  value?: string;
  onChanged?: (content: string) => void;
}

export default class Textfield extends Component<TextfieldProps> {
  state = {
    value: this.props.value,
  };

  componentWillReceiveProps(props: TextfieldProps): boolean {
    if (props.value !== undefined) {
      this.state.value = props.value;
    }
    return true;
  }

  onInput = (e: any) => {
    const { value } = e.target;
    this.setState({ value });
    this.props.onChanged?.call(null, value);
  };

  render(): ComponentChild {
    return (
      <>
        <input
          class={`${this.props.class} ${styles.input}`}
          placeholder={this.props.placeholder}
          value={this.state.value}
          onInput={this.onInput}
        ></input>
      </>
    );
  }
}
