import React from 'react';
import Card from '@material-ui/core/Card';
import Button from '@material-ui/core/Button';
import CardContent from '@material-ui/core/CardContent';
import Typography from '@material-ui/core/Typography';
import {getws} from "./WebSocket";

class StockCard extends React.Component {
    constructor(props) {
        super(props);
        this.state = {hidden: false};
        this.removeStock = this.removeStock.bind(this);
    }

    removeStock() {
        let ws = getws();
        ws.send(JSON.stringify({"symbol": this.props.symbol, "action": "remove"}));
        this.setState({hidden: true})
    }

    render() {
        const self = this;
        if (self.props.price) {
            return (
                <Card style={!self.state.hidden ? {} : {display: 'none'}}>
                    <CardContent>
                        <br/>
                        <Typography color="textSecondary">
                            {self.props.symbol}: {self.props.name}
                        </Typography>
                        <Typography color="textSecondary">
                            {self.props.importance}
                        </Typography>
                        <Typography variant="body2" component="p">
                            Current stock price: {self.props.price}
                            <br/>
                        </Typography>
                        <Button variant="contained" color="primary" onClick={this.removeStock}>
                            Remove
                        </Button>
                    </CardContent>
                </Card>
            )
        } else {
            return (
                <div>
                    Loading...
                </div>
            )
        }
    }
}

export default StockCard