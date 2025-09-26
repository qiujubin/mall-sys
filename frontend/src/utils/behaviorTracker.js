// 客户行为追踪工具
class BehaviorTracker {
  constructor() {
    this.sessionId = this.getOrCreateSessionId();
    this.userId = this.getUserId();
    this.baseUrl = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8088/api';
  }

  // 获取或创建会话ID
  getOrCreateSessionId() {
    let sessionId = localStorage.getItem('session_id');
    if (!sessionId) {
      sessionId = 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
      localStorage.setItem('session_id', sessionId);
    }
    return sessionId;
  }

  // 获取用户ID
  getUserId() {
    const userInfo = localStorage.getItem('user_info');
    if (userInfo) {
      try {
        return JSON.parse(userInfo).id;
      } catch (e) {
        return null;
      }
    }
    return null;
  }

  // 获取设备信息
  getDeviceInfo() {
    const userAgent = navigator.userAgent;
    let deviceType = 'pc';
    let browser = 'Unknown';
    let os = 'Unknown';

    // 检测设备类型
    if (/Mobile|Android|iPhone|iPad/.test(userAgent)) {
      deviceType = 'mobile';
    } else if (/iPad/.test(userAgent)) {
      deviceType = 'tablet';
    }

    // 检测浏览器
    if (userAgent.includes('Chrome')) {
      browser = 'Chrome';
    } else if (userAgent.includes('Firefox')) {
      browser = 'Firefox';
    } else if (userAgent.includes('Safari')) {
      browser = 'Safari';
    } else if (userAgent.includes('Edge')) {
      browser = 'Edge';
    }

    // 检测操作系统
    if (userAgent.includes('Windows')) {
      os = 'Windows';
    } else if (userAgent.includes('Mac')) {
      os = 'macOS';
    } else if (userAgent.includes('Linux')) {
      os = 'Linux';
    } else if (userAgent.includes('Android')) {
      os = 'Android';
    } else if (userAgent.includes('iOS')) {
      os = 'iOS';
    }

    return { deviceType, browser, os };
  }

  // 发送行为数据
  async trackBehavior(behaviorData) {
    try {
      const deviceInfo = this.getDeviceInfo();
      const data = {
        userId: this.userId,
        sessionId: this.sessionId,
        ...behaviorData,
        userAgent: navigator.userAgent,
        ...deviceInfo,
        pageUrl: window.location.href,
        referrerUrl: document.referrer,
        createTime: new Date().toISOString()
      };

      // 发送到后端
      const response = await fetch(`${this.baseUrl}/behavior/track`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
      });

      if (!response.ok) {
        console.warn('行为追踪失败:', response.statusText);
      }
    } catch (error) {
      console.warn('行为追踪错误:', error);
    }
  }

  // 页面浏览
  trackPageView(pageName, pageUrl = null) {
    this.trackBehavior({
      behaviorType: 'view',
      targetType: 'page',
      targetName: pageName,
      pageUrl: pageUrl || window.location.href
    });
  }

  // 商品浏览
  trackGoodsView(goodsId, goodsName, categoryId = null, brandId = null) {
    this.trackBehavior({
      behaviorType: 'view',
      targetType: 'goods',
      targetId: goodsId,
      targetName: goodsName,
      extraData: {
        categoryId,
        brandId
      }
    });
  }

  // 商品点击
  trackGoodsClick(goodsId, goodsName, categoryId = null, brandId = null) {
    this.trackBehavior({
      behaviorType: 'click',
      targetType: 'goods',
      targetId: goodsId,
      targetName: goodsName,
      extraData: {
        categoryId,
        brandId
      }
    });
  }

  // 商品收藏
  trackGoodsCollect(goodsId, goodsName, categoryId = null, brandId = null) {
    this.trackBehavior({
      behaviorType: 'collect',
      targetType: 'goods',
      targetId: goodsId,
      targetName: goodsName,
      extraData: {
        categoryId,
        brandId
      }
    });
  }

  // 加入购物车
  trackCartAdd(goodsId, goodsName, quantity = 1, price = null, categoryId = null, brandId = null) {
    this.trackBehavior({
      behaviorType: 'cart',
      targetType: 'goods',
      targetId: goodsId,
      targetName: goodsName,
      extraData: {
        categoryId,
        brandId,
        quantity,
        price
      }
    });
  }

  // 下单
  trackOrder(orderId, orderNo, totalAmount, items = []) {
    this.trackBehavior({
      behaviorType: 'order',
      targetType: 'order',
      targetId: orderId,
      targetName: orderNo,
      extraData: {
        totalAmount,
        items
      }
    });
  }

  // 退货
  trackReturn(orderId, orderNo, reason = null) {
    this.trackBehavior({
      behaviorType: 'return',
      targetType: 'order',
      targetId: orderId,
      targetName: orderNo,
      extraData: {
        reason
      }
    });
  }

  // 推荐点击
  trackRecommendClick(recommendId, recommendType, targetId, targetName) {
    this.trackBehavior({
      behaviorType: 'recommend',
      targetType: 'goods',
      targetId: targetId,
      targetName: targetName,
      extraData: {
        recommendId,
        recommendType
      }
    });
  }

  // 搜索
  trackSearch(keyword, resultsCount = 0) {
    this.trackBehavior({
      behaviorType: 'search',
      targetType: 'search',
      targetName: keyword,
      extraData: {
        resultsCount
      }
    });
  }
}

// 创建全局实例
const behaviorTracker = new BehaviorTracker();

export default behaviorTracker;
