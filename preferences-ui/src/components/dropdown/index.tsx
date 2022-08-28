import { Component, ComponentChild, Fragment, h } from "preact";
import SvgIcon from "../svgIcon";
import styles from "./index.module.css";

export interface DropdownProps {
  items: Array<string>;
  selected: number;
}

interface DropdownItem {
  index: number;
  content: string;
  onClicked?: (e: Event, index: number) => void;
}

export default class Dropdown extends Component<DropdownProps> {
  state = {
    isExpanded: false,
    current: 0,
  };

  constructor() {
    super();
  }

  componentDidMount() {
    this.setState({
      current: this.props.selected,
    });
    document.addEventListener("click", () => {
      if (this.state.isExpanded) {
        this.setState({
          isExpanded: false,
        });
      }
    });
  }

  itemClicked = (e: Event, index: number) => {
    this.setState({
      current: index,
    });
  };

  clicked = (e: Event) => {
    e.stopImmediatePropagation();
    this.setState((prev: any) => ({ isExpanded: !prev.isExpanded }));
  };

  render(): ComponentChild {
    let trigger = styles.dropdown_collapsed;
    let expandIcon = "rotate-0";
    if (this.state.isExpanded) {
      trigger = styles.dropdown_expanded;
      expandIcon = "rotate-180";
    }

    return (
      <>
        <div class={styles.dropdown_outer}>
          <div onClick={this.clicked} class={styles.dropdown_selected}>
            {this.props.items[this.state.current]}
            <SvgIcon
              name="arrow_drop_down"
              class={`h-4 w-4 fill-primary-900 transition-transform ${expandIcon}`}
            />
          </div>
          <div class={trigger}>
            {this.props.items.map((it: string, index: number) => (
              <Fragment key={index}>
                {h(dropdownItem, {
                  index: index,
                  content: it,
                  onClicked: this.itemClicked,
                })}
              </Fragment>
            ))}
          </div>
        </div>
      </>
    );
  }
}

function dropdownItem(props: DropdownItem) {
  return (
    <>
      <p
        class="py-1 px-4 whitespace-nowrap cursor-pointer transition-colors duration-300 hover:bg-primary-50 "
        onClick={(e: Event) => props.onClicked?.call(null, e, props.index)}
      >
        {props.content}
      </p>
    </>
  );
}
