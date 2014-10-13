/** @jsx React.DOM */
var Spinner = React.createClass({displayName: 'Spinner',
  propTypes: {
    min: React.PropTypes.number,
    max: React.PropTypes.number,
    value: React.PropTypes.number,
    valueChanged: React.PropTypes.func
  },

  getDefaultProps: function () {
    return {min: 0, max: 999, value: 0, valueChanged: function () {
    }};
  },

  getInitialState: function () {
    return {value: this.props.value};
  },

  componentWillReceiveProps: function (newProps) {
    this.setState({value: newProps.value});
  },

  changeValue: function (value) {
    if (value < this.props.min) {
      value = this.props.max;
    } else if (value > this.props.max) {
      value = this.props.min;
    }

    this.setState({value: value});
    this.props.valueChanged({value: value, ref: this.ref});
  },

  valueChanged: function () {
    var v = parseInt(this.refs.value.getDOMNode().value);
    if (isNaN(v)) {
      v = this.props.min;
    }
    if (v >= this.props.min && v <= this.props.max) {
      this.changeValue(v);
    }
  },

  increment: function () {
    this.changeValue(this.state.value + 1);
  },

  decrement: function () {
    this.changeValue(this.state.value - 1);
  },

  render: function () {
    return (
      React.DOM.div( {className:"input-group input-append spinner"}, 
        React.DOM.input( {type:"text", ref:"value",
        disabled:this.props.disabled,
        onChange:this.valueChanged, value:this.state.value}),
        React.DOM.span( {className:"input-group-btn-vertical add-on"}, 
          React.DOM.button( {type:"button", className:"btn btn-default up",
          disabled:this.props.disabled,
          onClick:this.increment}, 
            React.DOM.i( {className:"fa fa-caret-up"})
          ),
          React.DOM.button( {type:"button", className:"btn btn-default down",
          disabled:this.props.disabled,
          onClick:this.decrement}, 
            React.DOM.i( {className:"fa fa-caret-down"})
          )
        )
      ));
  }
});

var timezones = ["US/Hawaii", "US/Alaska", "US/Pacific", "US/Mountain",
  "US/Central", "US/Eastern", "GMT+1 ", "GMT+2 ", "GMT+3",
  "GMT+4", "GMT+5", "GMT+6", "GMT+7", "GMT+8", "GMT+9",
  "GMT+10", "GMT+11", "GMT+12", "GMT", "GMT-1", "GMT-2",
  "GMT-3", "GMT-4", "GMT-5", "GMT-6", "GMT-7", "GMT-8",
  "GMT-9", "GMT-10", "GMT-11", "GMT-12"];

var QueueSettings = React.createClass({displayName: 'QueueSettings',

  getDefaultProps: function () {
    return {valueChanged: function () {
    }};
  },

  value: function (ref) {
    return this.refs[ref].getDOMNode().value;
  },

  checked: function (ref) {
    return this.refs[ref].getDOMNode().checked;
  },

  setASAP: function () {
    var queue = this.props.queue;
    queue.asap = this.checked("asap");
    this.props.valueChanged("asap", queue);
  },

  setRandom: function () {
    var queue = this.props.queue;
    queue.random = this.checked("random");
    this.props.valueChanged("random", queue);
  },

  startHourChanged: function (v) {
    var queue = this.props.queue;
    queue.start.hour = v.value;
    this.props.valueChanged("start.hour", queue);
  },

  startMinChanged: function (v) {
    var queue = this.props.queue;
    queue.start.min = v.value;
    this.props.valueChanged("start.min", queue);
  },

  startAMChanged: function () {
    var queue = this.props.queue;
    queue.start.am = this.value("start-am");
    this.props.valueChanged("start.am", queue);
  },

  startTimezoneChanged: function () {
    var queue = this.props.queue;
    queue.start.timezone = this.value("start-timezone");
    queue.end.timezone = queue.start.timezone;
    this.props.valueChanged("start.timezone", queue);
  },

  endHourChanged: function (v) {
    var queue = this.props.queue;
    queue.end.hour = v.value;
    this.props.valueChanged("end.hour", queue);
  },

  endMinChanged: function (v) {
    var queue = this.props.queue;
    queue.end.min = v.value;
    this.props.valueChanged("end.min", queue);
  },

  endAMChanged: function () {
    var queue = this.props.queue;
    queue.end.am = this.value("end-am");
    console.log(queue.end.am);
    this.props.valueChanged("end.am", queue);
  },

  endFromChanged: function (v) {
    var queue = this.props.queue;
    queue.end.from = v.value;
    this.props.valueChanged("end.from", queue);
  },

  endToChanged: function (v) {
    var queue = this.props.queue;
    queue.end.to = v.value;
    this.props.valueChanged("end.to", queue);
  },

  render: function () {
    var state = this.props.queue;
    var startTimezoneOptions = [];
    var endTimezoneOptions = [];
    for (var i = 0; i < timezones.length; ++i) {
      var timezone = timezones[i];
      startTimezoneOptions.push(
        React.DOM.option( {key:i, value:timezone}, timezone));
      endTimezoneOptions.push(
        React.DOM.option( {key:i, value:timezone}, timezone));
    }

    return (
      React.DOM.form( {className:"form-horizontal"}, 
        React.DOM.div( {className:"control-group"}, 
          React.DOM.label( {className:"control-label"}, "Start"),
          React.DOM.div( {className:"controls"}, 
            React.DOM.label( {className:"radio queue-start"}, 
              React.DOM.input( {type:"radio", onChange:this.setASAP, name:"asap",
              checked:!state.asap}),
              Spinner( {min:1, max:12, disabled:state.asap,
              value:state.start.hour,
              valueChanged:this.startHourChanged, ref:"start-hour"}),
              Spinner( {min:1, max:59, disabled:state.asap,
              value:state.start.min,
              valueChanged:this.startMinChanged, ref:"start-min"}),
              React.DOM.div( {className:"form-control"}, 
                React.DOM.select( {disabled:state.asap,
                onChange:this.startAMChanged,
                value:state.start.am, ref:"start-am"}, 
                  React.DOM.option( {value:"AM"}, "AM"),
                  React.DOM.option( {value:"PM"}, "PM")
                ),
                React.DOM.select( {className:"queue-timezone", disabled:state.asap,
                onChange:this.startTimezoneChanged,
                value:state.start.timezone, ref:"start-timezone"}, 
                 startTimezoneOptions
                )
              )
            ),
            React.DOM.label( {className:"radio"}, 
              React.DOM.input( {type:"radio", onChange:this.setASAP, ref:"asap", name:"asap",
              checked:state.asap}),
            "ASAP"
            )
          )
        ),
        React.DOM.div( {className:"control-group"}, 
          React.DOM.label( {className:"control-label"}, "End"),
          React.DOM.div( {className:"controls"}, 
            React.DOM.label( {className:"radio queue-end-time"}, 
              React.DOM.input( {type:"radio", onChange:this.setRandom, name:"random",
              checked:!state.random}),
              Spinner( {min:1, max:12, disabled:state.random,
              value:state.end.hour,
              valueChanged:this.endHourChanged, ref:"end-hour"}),
              Spinner( {min:1, max:59, disabled:state.random,
              value:state.end.min,
              valueChanged:this.endMinChanged, ref:"end-min"}),
              React.DOM.select( {className:"form-control",
              onChange:this.endAMChanged,
              disabled:state.random,
              value:state.end.am, ref:"end-am"}, 
                React.DOM.option( {value:"AM"}, "AM"),
                React.DOM.option( {value:"PM"}, "PM")
              ),
              React.DOM.select( {className:"form-control queue-timezone",
              disabled:true,
              value:state.end.timezone, ref:"end-timezone"}, 
                 endTimezoneOptions
              )
            ),
            React.DOM.label( {className:"radio queue-end-random"}, 
              React.DOM.input( {type:"radio", onChange:this.setRandom, name:"random",
              checked:state.random, ref:"random"}),
              React.DOM.span( {className:"text"}, "Random every"),
              Spinner( {min:1, max:60, disabled:!state.random,
              value:state.end.from,
              valueChanged:this.endFromChanged, ref:"end-from"}),
              React.DOM.span( {className:"text"},  " to " ),
              Spinner( {min:1, max:60, disabled:!state.random,
              value:state.end.to,
              valueChanged:this.endToChanged, ref:"end-to"})
            )
          )
        )
      ));
  }
});

var queue = new QueueManager(document, "http://testing.com", true);

var QueueWidget = React.createClass({displayName: 'QueueWidget',
  initialize: function (queue) {
    return jQuery.extend(true, {}, queue);
  },

  queueLoaded: function () {
    this.setState({qm: this.state.qm, queue: this.initialize(this.state.qm.queue)});
    var publishingZone = document.getElementById('publishingZone');
    if (publishingZone) {
	    var zoneManager = Tapestry.findZoneManagerForZone('publishingZone'); // FIXME: pass this through parameter.
	    if (zoneManager) {
	    	zoneManager.updateFromURL(this.props.updateQueueViewURL);
	    }
	    else {
	    	console.log('Zone manager for publishingZone not found');
	    }
	}
	else {
		console.log('Element publishingZone not found');
	}
  },

  getInitialState: function () {
    var self = this;
    var qm = new QueueManager(this.props.callbacks, this.props.lazyLoad);
    this.setState({qm: qm});
    jQuery(qm).on("queue:loaded", this.queueLoaded);
    return {queue: qm.queue, qm: qm};
  },

  componentDidMount: function () {
    var self = this;
    jQuery(this.refs["settings-modal"].getDOMNode()).on("show.bs.modal", function () {
      self.setState({queue: self.initialize(self.state.qm.queue), valueChanged: false});
    });
  },

  makeTime: function (timeObject) {
    var d = new Date();
    var hours = parseInt(timeObject.hour, 10);
    var pm = timeObject.am === "PM";
    if (hours == 12 && !pm) {
      hours = 0;
    }
    else {
      hours += (hours < 12 && pm) ? 12 : 0;
    }

    d.setHours(hours);
    d.setMinutes(timeObject.min);
    return d;
  },

  makeTimeObject: function(time){
    var hours = time.getHours();
    if (hours > 12) {
      am = "PM";
      hours -= 12;
    } else if (hours == 12){
      am = "PM";
    }else if (hours == 0){
      hours = 12;
      am = "AM";
    }else {
      am = "AM";
    }
    return {am : am, hour: hours, min : time.getMinutes()};
  },

  validateStartEndTime: function (queue, isStart) {
    if (!queue.asap && !queue.random) {
      var startTime = this.makeTime(queue.start);
      var endTime = this.makeTime(queue.end);
      var oneHour = (30 * 60 * 1000);

      if (startTime > endTime) {
        if(isStart){
        endTime.setTime(startTime.getTime() + oneHour);
          $.extend(queue.end, this.makeTimeObject(endTime));
        }else {
          startTime.setTime(endTime.getTime() - oneHour);
          $.extend(queue.start, this.makeTimeObject(startTime));
        }

      }
    }
  },

  validateRandomTime: function (queue, isFrom) {
    if (parseInt(queue.end.from) > (parseInt(queue.end.to))) {
      if (isFrom) {
        queue.end.to = queue.end.from;
      } else {
        queue.end.from = queue.end.to;
      }
    }
  },

  queueChanged: function (property, queue) {
    this.validateStartEndTime(queue, property.indexOf("start.") == 0);
    this.validateRandomTime(queue, property == "end.from");
    this.setState({queue: queue, valueChanged: true});
  },

  saveChanges: function () {
    this.state.qm.saveSettings(this.state.queue);
    console.log(this.state.qm.queue);
    jQuery(this.refs["settings-modal"].getDOMNode()).modal("hide");
  },

  pauseToggle: function (e) {
    e.preventDefault();
    var qm = this.state.qm;
    qm = qm.isActive() ? qm.pause() : qm.resume();
    this.setState({qm: qm});
  },

  render: function () {
    var pauseIcon = this.state.qm.isActive() ?
      "icon icon-pause blink" :
      "icon icon-play";
    return (
      React.DOM.div( {className:"queue-settings"}, 
        React.DOM.span( {className:"timer"}),
        React.DOM.a( {'data-toggle':"modal", 'data-target':".settings-modal"}, 
          React.DOM.span( {className:"icon icon-cog"})
        ),
        React.DOM.a( {className:"pause", href:"#", onClick:this.pauseToggle}, 
          React.DOM.span( {className:pauseIcon})
        ),
        React.DOM.div( {tabIndex:"-1", role:"dialog", ref:"settings-modal",
        className:"settings-modal modal fade"}, 
          React.DOM.div( {className:"modal-header"}, 
            React.DOM.button( {type:"button", className:"close",
            'data-dismiss':"modal", 'aria-hidden':"true"}, "x"),
            React.DOM.h4( {className:"modal-title"}, "Settings")
          ),
          React.DOM.div( {className:"modal-body"}, 
            QueueSettings( {queue:this.state.queue,
            valueChanged:this.queueChanged})
          ),
          React.DOM.div( {className:"modal-footer"}, 
            React.DOM.button( {type:"button", className:"btn btn-default",
            'data-dismiss':"modal"}, "Close"),
            React.DOM.button( {type:"button", className:"save btn btn-primary",
            disabled:!this.state.valueChanged,
            onClick:this.saveChanges}, "Save changes")
          )
        )
      ))
  }
});



