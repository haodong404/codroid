import { Component, ComponentChild, h } from "preact";
import SvgIcon from "../SvgIcon";
import styles from "./index.module.css";

export default class Dropdown extends Component {
  state = {
    isExpanded: false,
  };

  constructor() {
    super();
  }

  componentDidMount() {
    document.addEventListener("click", () => {
      console.log("hello");

      this.setState({
        isExpanded: false,
      });
    });
  }

  clicked = (e: Event) => {
    e.stopImmediatePropagation();
    this.setState((prev: any) => ({ isExpanded: !prev.isExpanded }));
  };

  render(): ComponentChild {
    let trigger = "hidden";
    let expandIcon = "rotate-0";
    if (this.state.isExpanded) {
      trigger = "block";
      expandIcon = "rotate-180";
    }
    return (
      <>
        <div>
          <div onClick={this.clicked} class={styles.dropdown_selected}>
            Selected
            <SvgIcon
              name="arrow_drop_down"
              class={`h-4 w-4 fill-primary-900 ${expandIcon}`}
            />
          </div>
          <div class={`${styles.dropdown_expanded} ${trigger}`}>
            <ul>
              <li>Hello</li>
              <li>Hello</li>
              <li>Hello</li>
            </ul>
          </div>
        </div>
      </>
    );
  }
}
