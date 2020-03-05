import React from 'react';
import CardContent from '@material-ui/core/CardContent';
import TextField from '@material-ui/core/TextField';
import {getws} from "./WebSocket";

class AddStock extends React.Component {
    constructor(props) {
        super(props);
        this.addStock = this.addStock.bind(this);
        this.state = {value: ''};
        this.handleChange = this.handleChange.bind(this);
        this.keyPress = this.keyPress.bind(this);
    }

    handleChange(e) {
        this.setState({value: e.target.value});
    }

    keyPress(e) {
        if (e.key === 'Enter') {
            this.addStock()
        }
    }

    addStock() {
        const ws = getws();
        ws.send(JSON.stringify({"symbol": this.state.value, "action": "add"}))
    }

    render() {
        return (
            <CardContent>
                <TextField onKeyDown={this.keyPress} onChange={this.handleChange} id="standard-basic"
                           label="Add your own stock?"/>
            </CardContent>
        )
    }
}

export default AddStock