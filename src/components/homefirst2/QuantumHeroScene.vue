<template>
  <div ref="container" class="quantum-scene-container"></div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref } from 'vue';
import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';

const emit = defineEmits(['navigate', 'hover']);

const container = ref(null);
let scene, camera, renderer, controls;
let globe, globePoints;
let atmosphere;
let orbitalRings = [];
let dataParticles;
let animationId;
let clock;
let raycaster;
let pointer;
let interactiveObjects = [];
let hoveredHotspot = null;
let pointerState = { x: 0, y: 0, hasMoved: false };
let pointerDownState = { x: 0, y: 0, t: 0 };
let hotspots = [];
let simTime = 0;
let motionSpeed = 1;
let targetMotionSpeed = 1;
let lastHoverHitAt = 0;
let hoverCandidate = null;
let hoverCandidateAt = 0;
let glowTexture = null;

const HOVER_HOLD_MS = 140;
const HOVER_SWITCH_MS = 70;

const HERO_SCALE = 1.2;

// Brand Colors
const COLORS = {
  indigo: 0x4F46E5,
  purple: 0x9333EA,
  gold: 0xC5A059,
  white: 0xFFFFFF,
  blue: 0x60A5FA
};

function getGlowTexture() {
  if (glowTexture) return glowTexture;
  const size = 256;
  const canvas = document.createElement('canvas');
  canvas.width = size;
  canvas.height = size;
  const ctx = canvas.getContext('2d');
  const g = ctx.createRadialGradient(size / 2, size / 2, 0, size / 2, size / 2, size / 2);
  g.addColorStop(0, 'rgba(255,255,255,1)');
  g.addColorStop(0.18, 'rgba(255,255,255,0.85)');
  g.addColorStop(0.42, 'rgba(255,255,255,0.35)');
  g.addColorStop(1, 'rgba(255,255,255,0)');
  ctx.clearRect(0, 0, size, size);
  ctx.fillStyle = g;
  ctx.fillRect(0, 0, size, size);

  const tex = new THREE.CanvasTexture(canvas);
  tex.colorSpace = THREE.SRGBColorSpace;
  tex.minFilter = THREE.LinearMipmapLinearFilter;
  tex.magFilter = THREE.LinearFilter;
  tex.generateMipmaps = true;
  glowTexture = tex;
  return tex;
}

function getDeeperColor(hex) {
  const c = new THREE.Color(hex);
  const hsl = {};
  c.getHSL(hsl);
  c.setHSL(hsl.h, Math.min(1, hsl.s * 1.08), Math.max(0, hsl.l * 0.72));
  return c;
}

onMounted(() => {
  initScene();
  animate();
  window.addEventListener('resize', onWindowResize);
  window.addEventListener('pointermove', onWindowPointerMove, { passive: true });
});

onBeforeUnmount(() => {
  if (animationId) {
    cancelAnimationFrame(animationId);
  }
  window.removeEventListener('resize', onWindowResize);
  window.removeEventListener('pointermove', onWindowPointerMove);
  if (renderer?.domElement) {
    renderer.domElement.removeEventListener('pointerdown', onCanvasPointerDown);
    renderer.domElement.removeEventListener('pointerup', onCanvasPointerUp);
  }
  if (document.body.style.cursor === 'pointer') {
    document.body.style.cursor = '';
  }
  if (renderer) {
    renderer.dispose();
  }
});

function initScene() {
  if (!container.value) return;

  clock = new THREE.Clock();
  raycaster = new THREE.Raycaster();
  pointer = new THREE.Vector2();
  simTime = 0;
  motionSpeed = 1;
  targetMotionSpeed = 1;

  // Scene setup
  scene = new THREE.Scene();
  scene.background = new THREE.Color(0xF9F8F4); // Keep original background or make it transparent
  // scene.fog = new THREE.FogExp2(0xF9F8F4, 0.02);

  // Camera
  camera = new THREE.PerspectiveCamera(45, container.value.clientWidth / container.value.clientHeight, 0.1, 1000);
  camera.position.set(0, 2, 9.5);
  camera.lookAt(0, 0, 0);

  // Renderer
  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
  renderer.setSize(container.value.clientWidth, container.value.clientHeight);
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  container.value.appendChild(renderer.domElement);
  renderer.domElement.style.touchAction = 'none';
  renderer.domElement.addEventListener('pointerdown', onCanvasPointerDown);
  renderer.domElement.addEventListener('pointerup', onCanvasPointerUp);

  // Controls
  controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  controls.dampingFactor = 0.05;
  controls.enableZoom = false;
  controls.enablePan = false;
  controls.enableRotate = true;
  controls.rotateSpeed = 0.7;
  controls.autoRotate = true;
  controls.autoRotateSpeed = 1.0;

  // Lighting
  const ambientLight = new THREE.AmbientLight(0xffffff, 0.6);
  scene.add(ambientLight);

  const dirLight = new THREE.DirectionalLight(0xffffff, 1);
  dirLight.position.set(5, 5, 5);
  scene.add(dirLight);

  const purpleLight = new THREE.PointLight(COLORS.purple, 2, 20);
  purpleLight.position.set(-5, 2, -5);
  scene.add(purpleLight);

  const goldLight = new THREE.PointLight(COLORS.gold, 2, 20);
  goldLight.position.set(5, -2, 5);
  scene.add(goldLight);

  // Objects
  createDigitalGlobe();
  createAtmosphere();
  createOrbitalRings();
  createNavigationHotspots();
  createFloatingDataParticles();
}

function createDigitalGlobe() {
  // 1. Wireframe Globe
  const geometry = new THREE.IcosahedronGeometry(2 * HERO_SCALE, 4); // High detail for smooth sphere

  // Wireframe material
  const wireframeMaterial = new THREE.MeshBasicMaterial({
    color: COLORS.indigo,
    wireframe: true,
    transparent: true,
    opacity: 0.3 // Increased opacity from 0.15 to 0.3
  });

  globe = new THREE.Mesh(geometry, wireframeMaterial);
  scene.add(globe);

  // 2. Points on vertices (The "Digital" nodes)
  // Extract vertices positions
  const vertices = [];
  const posAttribute = geometry.attributes.position;

  for (let i = 0; i < posAttribute.count; i++) {
    const x = posAttribute.getX(i);
    const y = posAttribute.getY(i);
    const z = posAttribute.getZ(i);
    // Add some randomness to only pick some points, or all
    if (Math.random() > 0.5) {
      vertices.push(x, y, z);
    }
  }

  const pointsGeometry = new THREE.BufferGeometry();
  pointsGeometry.setAttribute('position', new THREE.Float32BufferAttribute(vertices, 3));

  const pointsMaterial = new THREE.PointsMaterial({
    color: COLORS.indigo,
    size: 0.05 * HERO_SCALE, // Slightly larger points
    transparent: true,
    opacity: 0.8, // Increased opacity
    sizeAttenuation: true
  });

  globePoints = new THREE.Points(pointsGeometry, pointsMaterial);
  globe.add(globePoints); // Add to globe so they rotate together
}

function createAtmosphere() {
  // A glowing inner sphere
  const geometry = new THREE.SphereGeometry(1.95 * HERO_SCALE, 64, 64);
  const material = new THREE.MeshBasicMaterial({
    color: COLORS.blue,
    transparent: true,
    opacity: 0.15, // Increased opacity from 0.05 to 0.15 for stronger core color
    side: THREE.BackSide // Render on inside
  });

  atmosphere = new THREE.Mesh(geometry, material);
  scene.add(atmosphere);
}

function createOrbitalRings() {
  // Create rings to represent orbits/connections/AI processing
  const createRing = (radius, color, rotationX, rotationY) => {
    const scaledRadius = radius * HERO_SCALE;

    // Main Ring - Tube geometry for 3D volume instead of flat ring
    const geometry = new THREE.TorusGeometry(scaledRadius, 0.04 * HERO_SCALE, 10, 140);
    const material = new THREE.MeshPhysicalMaterial({
      color: color,
      transparent: true,
      opacity: 0.68,
      metalness: 0.8,
      roughness: 0.18,
      emissive: color,
      emissiveIntensity: 0.28,
      clearcoat: 0.6,
      clearcoatRoughness: 0.2
    });
    const ring = new THREE.Mesh(geometry, material);
    ring.rotation.x = rotationX;
    ring.rotation.y = rotationY;

    // Add a secondary thinner wireframe ring for tech detail
    const wireframeGeo = new THREE.TorusGeometry(scaledRadius, 0.06 * HERO_SCALE, 5, 56);
    const wireframeMat = new THREE.MeshBasicMaterial({
      color: color,
      wireframe: true,
      transparent: true,
      opacity: 0.18,
      blending: THREE.AdditiveBlending,
      depthWrite: false
    });
    const wireRing = new THREE.Mesh(wireframeGeo, wireframeMat);
    ring.add(wireRing);

    // Custom property for animation
    ring.userData = {
      rotationSpeed: (Math.random() * 0.2 + 0.1) * (Math.random() > 0.5 ? 1 : -1),
      baseOpacity: material.opacity,
      baseEmissiveIntensity: material.emissiveIntensity,
      baseColor: material.color.getHex(),
      baseEmissive: material.emissive.getHex(),
      wireRing,
      wireBaseOpacity: wireframeMat.opacity
    };

    scene.add(ring);
    orbitalRings.push(ring);
  };

  createRing(2.5, COLORS.purple, Math.PI / 2.2, 0);
  createRing(2.8, COLORS.gold, Math.PI / 1.8, Math.PI / 6);
  createRing(3.2, COLORS.indigo, Math.PI / 3, -Math.PI / 4);
}

function createNavigationHotspots() {
  const items = [
    { id: 'matching', label: '智能择校', path: '/matching', ringIndex: 0, color: COLORS.purple },
    { id: 'Country', label: '院校库', path: '/country', ringIndex: 1, color: COLORS.gold },
    { id: 'rebate', label: '入学奖励', path: '/r', ringIndex: 2, color: COLORS.indigo }
  ];

  const tex = getGlowTexture();

  items.forEach((item, i) => {
    const ring = orbitalRings[item.ringIndex];
    if (!ring) return;

    const radius = ring.geometry.parameters.radius;
    const group = new THREE.Group();

    const coreGeo = new THREE.SphereGeometry(0.09 * HERO_SCALE, 28, 28);
    const coreMat = new THREE.MeshStandardMaterial({
      color: item.color,
      emissive: item.color,
      emissiveIntensity: 1.1,
      metalness: 0.05,
      roughness: 0.38
    });
    const core = new THREE.Mesh(coreGeo, coreMat);

    const haloGeo = new THREE.SphereGeometry(0.16 * HERO_SCALE, 36, 36);
    const haloMat = new THREE.MeshStandardMaterial({
      color: item.color,
      emissive: item.color,
      emissiveIntensity: 0.9,
      map: tex,
      transparent: true,
      opacity: 0.35,
      blending: THREE.AdditiveBlending,
      depthWrite: false,
      metalness: 0,
      roughness: 1
    });
    const halo = new THREE.Mesh(haloGeo, haloMat);

    const haloOuterGeo = new THREE.SphereGeometry(0.215 * HERO_SCALE, 32, 32);
    const haloOuterMat = new THREE.MeshStandardMaterial({
      color: item.color,
      emissive: item.color,
      emissiveIntensity: 0.55,
      map: tex,
      transparent: true,
      opacity: 0.16,
      blending: THREE.AdditiveBlending,
      depthWrite: false,
      metalness: 0,
      roughness: 1
    });
    const haloOuter = new THREE.Mesh(haloOuterGeo, haloOuterMat);

    const hitGeo = new THREE.SphereGeometry(0.22 * HERO_SCALE, 18, 18);
    const hitMat = new THREE.MeshBasicMaterial({
      transparent: true,
      opacity: 0,
      depthWrite: false
    });
    const hit = new THREE.Mesh(hitGeo, hitMat);

    group.add(haloOuter);
    group.add(halo);
    group.add(core);
    group.add(hit);

    group.userData = {
      id: item.id,
      label: item.label,
      path: item.path,
      ring,
      core,
      halo,
      haloOuter,
      hit,
      radius,
      baseAngle: (Math.PI * 2 * i) / items.length + Math.PI / 6,
      speed: 0.45 + Math.random() * 0.35
    };

    group.position.x = Math.cos(group.userData.baseAngle) * radius;
    group.position.y = Math.sin(group.userData.baseAngle) * radius;

    core.userData.hotspot = group.userData;
    halo.userData.hotspot = group.userData;
    hit.userData.hotspot = group.userData;

    ring.add(group);
    hotspots.push(group);
    interactiveObjects.push(hit);
  });
}

function createFloatingDataParticles() {
  const geometry = new THREE.BufferGeometry();
  const count = 200;
  const positions = new Float32Array(count * 3);

  for (let i = 0; i < count; i++) {
    // Random position in a sphere shell around the globe
    const r = (3.5 + Math.random() * 2) * HERO_SCALE;
    const theta = Math.random() * 2 * Math.PI;
    const phi = Math.acos(2 * Math.random() - 1);

    positions[i * 3] = r * Math.sin(phi) * Math.cos(theta);
    positions[i * 3 + 1] = r * Math.sin(phi) * Math.sin(theta);
    positions[i * 3 + 2] = r * Math.cos(phi);
  }

  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));

  const material = new THREE.PointsMaterial({
    color: COLORS.gold,
    size: 0.05 * HERO_SCALE,
    transparent: true,
    opacity: 0.6
  });

  dataParticles = new THREE.Points(geometry, material);
  scene.add(dataParticles);
}

function animate() {
  animationId = requestAnimationFrame(animate);
  const delta = clock.getDelta();
  targetMotionSpeed = 1;
  if (hoveredHotspot) {
    targetMotionSpeed = 0.02;
  } else if (pointerState.hasMoved) {
    const r = Math.hypot(pointerState.x, pointerState.y);
    if (r <= 0.3) {
      targetMotionSpeed = 0.02;
    } else if (r >= 0.32 && r <= 0.72) {
      targetMotionSpeed = 0.02;
    } else if (r >= 0.22 && r <= 0.82) {
      targetMotionSpeed = 0.08;
    }
  }

  motionSpeed += (targetMotionSpeed - motionSpeed) * 0.15;
  const dt = delta * motionSpeed;
  simTime += dt;
  const t = simTime;

  // Rotate Globe
  if (globe) {
    globe.rotation.y = t * 0.1;
    if (pointerState.hasMoved) {
      globe.rotation.x += (pointerState.y * 0.14 - globe.rotation.x) * 0.06;
      globe.rotation.z += (pointerState.x * 0.12 - globe.rotation.z) * 0.06;
    }
  }

  // Animate Rings
  orbitalRings.forEach(ring => {
    ring.rotation.z += ring.userData.rotationSpeed * dt * 1.1;
    // Animate packet along the ring? 
    // Simplified: Just rotate the ring itself on Z axis (which is local up for Torus created in XY plane usually, but here we rotated X/Y)
    // Actually simpler to just rotate the ring mesh or a parent container.
    // Let's just rotate the ring object slightly to simulate orbit drift

    // Animate the packet position if we want it to orbit the ring
    // Since packet is child of ring, and ring is Trous in XY plane (default) then rotated.
    // We can rotate the packet around the ring center.
    // Packet is at (radius, 0, 0) initially.
  });

  hotspots.forEach((group) => {
    const { radius, baseAngle, speed, core, halo, haloOuter } = group.userData;
    const a = t * speed + baseAngle;
    group.position.x = Math.cos(a) * radius;
    group.position.y = Math.sin(a) * radius;
    const isHovered = hoveredHotspot?.id === group.userData.id;
    const pulse = 0.9 + Math.sin(t * 2.4 + baseAngle) * 0.12;
    core.scale.setScalar(pulse * (isHovered ? 1.2 : 1));
    halo.scale.setScalar(isHovered ? 1.12 : 1.02);
    haloOuter.scale.setScalar(isHovered ? 1.08 : 1.02);
    const amp = 0.07 + (isHovered ? 0.02 : 0);
    const s = (Math.sin(t * 2.8 + baseAngle) + 1) / 2;
    halo.material.opacity = (isHovered ? 0.42 : 0.28) + s * amp;
    haloOuter.material.opacity = (isHovered ? 0.22 : 0.14) + s * (amp * 0.65);
  });

  // Animate Particles
  if (dataParticles) {
    dataParticles.rotation.y = -t * 0.05; // Counter rotate

    // Pulse effect
    // Just slight float
    dataParticles.position.y = Math.sin(t * 0.5) * 0.1;
  }

  if (pointerState.hasMoved && raycaster && pointer && interactiveObjects.length) {
    updateHoverState();
  }

  if (controls) {
    controls.autoRotateSpeed = 1.0 * Math.max(0.02, motionSpeed);
  }
  if (controls) controls.update();
  if (renderer && scene && camera) renderer.render(scene, camera);
}

function onWindowPointerMove(e) {
  if (!renderer?.domElement) return;
  const rect = renderer.domElement.getBoundingClientRect();
  const inside =
    e.clientX >= rect.left &&
    e.clientX <= rect.right &&
    e.clientY >= rect.top &&
    e.clientY <= rect.bottom;

  if (!inside) {
    if (pointerState.hasMoved) {
      pointerState.hasMoved = false;
      hoverCandidate = null;
      hoverCandidateAt = 0;
      lastHoverHitAt = 0;
      setHoveredHotspot(null);
    }
    return;
  }

  const x = ((e.clientX - rect.left) / rect.width) * 2 - 1;
  const y = -(((e.clientY - rect.top) / rect.height) * 2 - 1);
  pointer.set(x, y);

  pointerState.x = x;
  pointerState.y = y;
  pointerState.hasMoved = true;
}

function onCanvasPointerDown(e) {
  pointerDownState = { x: e.clientX, y: e.clientY, t: performance.now() };
}

function onCanvasPointerUp(e) {
  if (!hoveredHotspot?.path) return;

  const dx = e.clientX - pointerDownState.x;
  const dy = e.clientY - pointerDownState.y;
  const dist = Math.hypot(dx, dy);
  const dt = performance.now() - pointerDownState.t;

  if (dist <= 8 && dt <= 450) {
    emit('navigate', hoveredHotspot.path);
  }
}

function updateHoverState() {
  const now = performance.now();
  raycaster.setFromCamera(pointer, camera);
  const hits = raycaster.intersectObjects(interactiveObjects, false);
  const hitHotspot = hits[0]?.object?.userData?.hotspot || null;

  if (hitHotspot) {
    lastHoverHitAt = now;
    if (!hoveredHotspot || hoveredHotspot.id === hitHotspot.id) {
      hoverCandidate = null;
      hoverCandidateAt = 0;
      setHoveredHotspot(hitHotspot);
      return;
    }

    if (!hoverCandidate || hoverCandidate.id !== hitHotspot.id) {
      hoverCandidate = hitHotspot;
      hoverCandidateAt = now;
      return;
    }

    if (now - hoverCandidateAt >= HOVER_SWITCH_MS) {
      setHoveredHotspot(hitHotspot);
    }
    return;
  }

  hoverCandidate = null;
  hoverCandidateAt = 0;
  if (hoveredHotspot && now - lastHoverHitAt < HOVER_HOLD_MS) return;
  setHoveredHotspot(null);
}

function setHoveredHotspot(next) {
  if (hoveredHotspot?.id === next?.id) return;

  if (hoveredHotspot) {
    const { core, halo, haloOuter, ring } = findHotspotGroup(hoveredHotspot);
    if (core) core.material.emissiveIntensity = 0.9;
    if (halo) {
      halo.material.emissiveIntensity = 0.9;
    }
    if (haloOuter) {
      haloOuter.material.emissiveIntensity = 0.55;
    }
    if (ring?.material) {
      ring.material.opacity = ring.userData.baseOpacity;
      ring.material.emissiveIntensity = ring.userData.baseEmissiveIntensity;
      ring.material.color.setHex(ring.userData.baseColor);
      ring.material.emissive.setHex(ring.userData.baseEmissive);
      ring.scale.setScalar(1);
      if (ring.userData.wireRing?.material) {
        ring.userData.wireRing.material.opacity = ring.userData.wireBaseOpacity;
      }
    }
  }

  hoveredHotspot = next;

  if (hoveredHotspot) {
    const { core, halo, haloOuter, ring } = findHotspotGroup(hoveredHotspot);
    if (core) core.material.emissiveIntensity = 1.35;
    if (halo) {
      halo.material.emissiveIntensity = 1.25;
    }
    if (haloOuter) {
      haloOuter.material.emissiveIntensity = 0.8;
    }
    if (ring?.material) {
      ring.material.opacity = Math.min(0.9, ring.userData.baseOpacity + 0.25);
      ring.material.emissiveIntensity = Math.min(0.75, ring.userData.baseEmissiveIntensity + 0.35);
      ring.material.color.copy(getDeeperColor(ring.userData.baseColor));
      ring.material.emissive.copy(getDeeperColor(ring.userData.baseEmissive));
      ring.scale.setScalar(1.012);
      if (ring.userData.wireRing?.material) {
        ring.userData.wireRing.material.opacity = Math.min(0.55, ring.userData.wireBaseOpacity + 0.22);
      }
    }

    document.body.style.cursor = 'pointer';
    emit('hover', { label: hoveredHotspot.label, path: hoveredHotspot.path });
    return;
  }

  document.body.style.cursor = '';
  emit('hover', null);
}

function findHotspotGroup(hotspotData) {
  const group = hotspots.find(h => h.userData.id === hotspotData.id) || null;
  return {
    core: group?.userData?.core || null,
    halo: group?.userData?.halo || null,
    haloOuter: group?.userData?.haloOuter || null,
    ring: group?.userData?.ring || null
  };
}

function onWindowResize() {
  if (!container.value || !camera || !renderer) return;

  camera.aspect = container.value.clientWidth / container.value.clientHeight;
  camera.updateProjectionMatrix();
  renderer.setSize(container.value.clientWidth, container.value.clientHeight);
}
</script>

<style scoped>
.quantum-scene-container {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 0;
  opacity: 0.8;
  pointer-events: auto;
  touch-action: none;
}
</style>
