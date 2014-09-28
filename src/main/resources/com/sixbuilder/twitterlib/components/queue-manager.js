var QueueManager = function (callbacks, lazyLoad) {
  this.callbacks = callbacks;
  this.queue = {
	queueType: 'TEST',
	userId: 'testUserId',
    start: {hour: 12, min: 0, am: "PM", timezone: "US/Pacific"},
    end: {hour: 12, min: 30, am: "PM", from: 2, to: 5, timezone:"US/Pacific"},
    asap: true,
    random: true,
    jobs: [],
    pause: true
  };

  if (!lazyLoad) {
    this.load();
  }

  return this;
};

QueueManager.prototype.loaded = function (callback) {
  var self = this;
  jQuery(this).on("queue:loaded", function () {
    callback(self);
  });
  return this;
};

QueueManager.prototype.error = function (callback) {
  var self = this;
  jQuery(this).on("queue:error", function () {
    callback(self);
  });
  return this;
};

QueueManager.prototype.errorHandler = function () {
  jQuery(this).trigger(jQuery.Event("queue:error"))
};

QueueManager.prototype.triggerLoaded = function () {
  jQuery(this).trigger(jQuery.Event("queue:loaded", {queue: this.queue}));
};

QueueManager.prototype.load = function () {
  var self = this;
  jQuery.getJSON(this.callbacks.get).
    done(function (resp) {
      if (resp.success) {
        self.queue = resp.queue;
        self.triggerLoaded();
      } else {
        self.errorHandler();
      }
    }).
    fail(function () {
      self.errorHandler();
    });
  return this;
}

QueueManager.prototype.updateQueue = function () {
  var self = this;
  jQuery.ajax(this.callbacks.update, {
    dataType: "json",
    type: "post",
    data: {"queue" : JSON.stringify(this.queue)}}).
    done(function (resp) {
      if (resp.success) {
        self.queue = resp.queue;
        self.triggerLoaded();
      } else {
        self.errorHandler();
      }
    }).
    fail(function () {
      self.errorHandler();
    });
};

QueueManager.prototype.enqueue = function (job) {
  this.queue.jobs.push(job);
  this.updateQueue();
  return this;
};

QueueManager.prototype.dequeue = function (job) {
  this.queue.jobs.shift();
  this.updateQueue();
  return this;
};

QueueManager.prototype.pause = function () {
  this.queue.pause = true;
  this.updateQueue();
  return this;
};

QueueManager.prototype.isActive = function () {
  return this.queue.pause == false;
};

QueueManager.prototype.is_empty = function () {
  return this.queue.length === 0;
};

QueueManager.prototype.resume = function () {
  this.queue.pause = false;
  this.updateQueue();
  return this;
};

QueueManager.prototype.saveSettings = function (settings) {
  jQuery.extend(this.queue, settings);
  this.updateQueue();
  return this;
};




